package com.example.myapplication.utils

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.lang.NullPointerException
import java.util.*

object StorageUtil {
    private val storageInstance:FirebaseStorage by lazy{
        FirebaseStorage.getInstance()
    }

    private val currentUserRef:StorageReference
    get() = storageInstance.reference.child(FirebaseAuth.getInstance().currentUser?.uid?: throw NullPointerException("UUID is Null"))

    fun uploadProfileImage(imageInBytes:ByteArray,
                           a_function_passed_from_outside_with_one_string_as_Input:(imagePath:String)->Unit){
        val ref = currentUserRef.child("profilePictures/${UUID.nameUUIDFromBytes(imageInBytes)}")
        ref.putBytes(imageInBytes)
            .addOnSuccessListener {
                a_function_passed_from_outside_with_one_string_as_Input(ref.path)
            }
    }

    fun uploadImageIntoMessages(imageInBytes:ByteArray,
                           a_function_passed_from_outside_with_one_string_as_Input:(imagePath:String)->Unit){
        val ref = currentUserRef.child("Messages/${UUID.nameUUIDFromBytes(imageInBytes)}")
        ref.putBytes(imageInBytes)
            .addOnSuccessListener {
                a_function_passed_from_outside_with_one_string_as_Input(ref.path)
            }
    }
    fun getImagePath(path:String) = storageInstance.getReference(path)


}