package com.example.myapplication.utils

import android.content.Context
import android.util.Log
import com.example.myapplication.`interface`.MessageType
import com.example.myapplication.items.imageMessages.ChatLeftImageItem
import com.example.myapplication.items.imageMessages.ChatRightImageItem
import com.example.myapplication.items.textMessages.ChatLeftItem
import com.example.myapplication.items.textMessages.ChatRightItem
import com.example.myapplication.items.userListDisplay.PersonItem
import com.example.myapplication.model.ChatChannel
import com.example.myapplication.model.ImageMessages
import com.example.myapplication.model.TextMessages
import com.example.myapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.kotlinandroidextensions.Item

object FireStoreUtil {
    val fireStroreInstance:FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    private val currentUserDocReference:DocumentReference
    get() = fireStroreInstance.document("Users/${FirebaseAuth.getInstance().currentUser?.uid ?: throw NullPointerException("Uid is Null")}")

    private val chatChannelCurrentRef = fireStroreInstance.collection("ChatChannels")

    fun initCurrentUserFirstTime(passingIntentFunction: ()->Unit){
        currentUserDocReference.get().addOnSuccessListener {
            if(!it.exists()){
                val newUser = User(FirebaseAuth.getInstance().currentUser?.displayName ?: "","",null, mutableListOf())
                currentUserDocReference.set(newUser).addOnSuccessListener {
                    passingIntentFunction()
                }

            }else{
                passingIntentFunction()
            }
        }
    }

    fun getUserImage(userId:String,after_get_Image_path_call_me:(imagePath:String)->Unit){
        fireStroreInstance.document("Users/$userId").get().addOnSuccessListener {
            if(it.exists()){
                after_get_Image_path_call_me(it["profileImagePath"] as String)
            }
        }
    }

    fun updateCurrentUser(name:String = "",bio:String = "",profilePath:String? = null){
        val userFieldMap = mutableMapOf<String,Any>()
        if(name.isNotBlank())userFieldMap["name"] = name
        if(bio.isNotBlank())userFieldMap["bio"] = bio
        if(profilePath!=null)userFieldMap["profileImagePath"] = profilePath
        currentUserDocReference.update(userFieldMap)
    }

    fun getCurrentUser(a_Function_Passed_From_OutSide_Which_Has_user_object_as_Input:(User) -> Unit){
        currentUserDocReference.get().addOnSuccessListener {
            if(it!=null){
                if(it.toObject(User::class.java)!=null)
                a_Function_Passed_From_OutSide_Which_Has_user_object_as_Input(it.toObject(User::class.java)!!)
                else
                 Log.d("==>","error")
            }

        }
    }
    
    fun userAddedListener(context: Context,onListener:(List<Item>)->Unit):ListenerRegistration{
        return fireStroreInstance.collection("Users").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException != null){
                Log.e("FIRESTORE","users listener error",firebaseFirestoreException)
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()
            querySnapshot?.documents?.forEach {
                if(it.id != FirebaseAuth.getInstance().currentUser?.uid)
                items.add(
                    PersonItem(
                        it.toObject(User::class.java)!!,
                        it.id,
                        context
                    )
                )
            }
            onListener(items)
        }
    }

    fun removeListener(register:ListenerRegistration) = register.remove()

    fun getOrCreateChatChannel(otherUserId:String,afterChannelCreatedOrFind:(channelId:String)->Unit){
        currentUserDocReference.collection("engagedChatChannels").document(otherUserId).get().addOnSuccessListener {
            if(it.exists()){
                afterChannelCreatedOrFind(it["channelId"] as String)
                return@addOnSuccessListener
            }
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid!!
            val newChatChannel = chatChannelCurrentRef.document()
            newChatChannel.set(ChatChannel(mutableListOf(currentUserId,otherUserId)))
            currentUserDocReference.collection("engagedChatChannels").document(otherUserId).set(mapOf("channelId" to newChatChannel.id))
            fireStroreInstance.collection("Users").document(otherUserId).collection("engagedChatChannels").document(currentUserId).set(mapOf("channelId" to newChatChannel.id))
            afterChannelCreatedOrFind(newChatChannel.id)
        }
    }

    fun addMessageChangeListener(channelId: String,context:Context,after_Get_Message_Pass_That_Here: (List<Item>) -> Unit):ListenerRegistration{
        return chatChannelCurrentRef.document(channelId).collection("Messages").orderBy("time").addSnapshotListener { querySnapshot, firebaseFirestoreException ->
            if(firebaseFirestoreException!=null)
            {
                Log.e("Error","firebase error occured: ",firebaseFirestoreException)
                return@addSnapshotListener
            }
            val items = mutableListOf<Item>()
            querySnapshot?.documents?.forEach {
                if(it["type"] == MessageType.TEXT){
                    if(it["senderId"]!!.equals(FirebaseAuth.getInstance().currentUser?.uid))
                        items.add(
                            ChatRightItem(
                                it.toObject(TextMessages::class.java)!!,
                                context
                            )
                        )
                    else
                        items.add(
                            ChatLeftItem(
                                it.toObject(TextMessages::class.java)!!,
                                context
                            )
                        )
                }
                else{
                    if(it["senderId"]!!.equals(FirebaseAuth.getInstance().currentUser?.uid))
                        items.add(
                            ChatRightImageItem(
                                it.toObject(ImageMessages::class.java)!!,
                                context
                            )
                        )
                    else
                        items.add(
                            ChatLeftImageItem(
                                it.toObject(ImageMessages::class.java)!!,
                                context
                            )
                        )
                }
            }
            after_Get_Message_Pass_That_Here(items)
        }
    }
    fun sendMessage(textMessages: TextMessages,channelId: String){
        chatChannelCurrentRef.document(channelId).collection("Messages").add(textMessages)
    }
    fun sendImageMessage(imageMessages: ImageMessages,channelId: String){
        chatChannelCurrentRef.document(channelId).collection("Messages").add(imageMessages)
    }


}