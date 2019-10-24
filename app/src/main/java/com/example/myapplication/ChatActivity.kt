package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.`interface`.MessageType
import com.example.myapplication.items.imageMessages.ChatLeftImageItem
import com.example.myapplication.items.imageMessages.ChatRightImageItem
import com.example.myapplication.items.textMessages.ChatLeftItem
import com.example.myapplication.items.textMessages.ChatRightItem
import com.example.myapplication.model.ImageMessages
import com.example.myapplication.model.TextMessages
import com.example.myapplication.utils.AppConstants
import com.example.myapplication.utils.FireStoreUtil
import com.example.myapplication.utils.StorageUtil
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ListenerRegistration
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Section
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.fragment_my_account.*
import org.jetbrains.anko.toast
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class ChatActivity : AppCompatActivity() {

    lateinit var messageListenerregistration:ListenerRegistration
    var isRecyclerViewInitialize = true
    lateinit var messageSection:Section
    lateinit var chatChannelId:String
    val RC_SELECT_IMAGE = 106

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(AppConstants.USER_NAME)
        val otherUserId = intent.getStringExtra(AppConstants.USER_ID)

        FireStoreUtil.getOrCreateChatChannel(otherUserId){channelId ->
            chatChannelId = channelId
            messageListenerregistration = FireStoreUtil.addMessageChangeListener(channelId,this){messageList ->
                messageList.forEach {
                    if(it is ChatLeftItem)
                    Log.d("chv==>","${it.toString1()}")
                    if(it is ChatRightItem)
                    Log.d("chv==>","${it.toString1()}")
                    if(it is ChatLeftImageItem)
                    Log.d("chv==>","${it.toString1()}")
                    if(it is ChatRightImageItem)
                    Log.d("chv==>","${it.toString1()}")
                }
                btn_send.setOnClickListener {
                    val messageTosend = TextMessages(et_message.text.toString(),Date(),FirebaseAuth.getInstance().currentUser?.uid!!,MessageType.TEXT)
                    et_message.text.clear()
                    FireStoreUtil.sendMessage(messageTosend,channelId)
                }

                if(isRecyclerViewInitialize){
                    val adapter = GroupAdapter<GroupieViewHolder>()
                    messageSection = Section(messageList)
                    adapter.add(messageSection)
                    rv_messages.adapter = adapter
                    isRecyclerViewInitialize = false
                }else{
                    messageSection.update(messageList)
                }

                rv_messages.scrollToPosition(rv_messages.adapter!!.itemCount -1)

            }
            fb_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"Select Image"),RC_SELECT_IMAGE)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(contentResolver,selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            val selectedImage = outputStream.toByteArray()
            StorageUtil.uploadImageIntoMessages(selectedImage){imagePath ->
                val messageTosend = ImageMessages(imagePath,Date(),FirebaseAuth.getInstance().currentUser?.uid!!,MessageType.IMAGE)
                FireStoreUtil.sendImageMessage(messageTosend,chatChannelId )
            }

        }
    }

}
