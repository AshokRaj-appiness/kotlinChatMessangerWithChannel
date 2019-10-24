package com.example.myapplication.fragments


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.myapplication.GlideApp
import com.example.myapplication.R
import com.example.myapplication.SignUpActivity
import com.example.myapplication.utils.FireStoreUtil
import com.example.myapplication.utils.StorageUtil
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.fragment_my_account.*
import kotlinx.android.synthetic.main.fragment_my_account.view.*
import org.jetbrains.anko.clearTask
import org.jetbrains.anko.newTask
import org.jetbrains.anko.support.v4.intentFor
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MyAccountFragment : Fragment() {

    private val RC_SELECT_IMAGE = 2
    private lateinit var selectedImage:ByteArray
    private var pictureJustChanged:Boolean = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view =  inflater.inflate(R.layout.fragment_my_account, container, false)
        view.apply {
            iv_profile_image.setOnClickListener {
                val intent = Intent().apply {
                    type = "image/*"
                    action = Intent.ACTION_GET_CONTENT
                    putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg","image/png"))
                }
                startActivityForResult(Intent.createChooser(intent,"Select Image"),RC_SELECT_IMAGE)
            }
            btn_save.setOnClickListener {
                if(::selectedImage.isInitialized)
                    StorageUtil.uploadProfileImage(selectedImage){ imagepath ->
                        FireStoreUtil.updateCurrentUser(et_name.text.toString(),et_bio.text.toString(),imagepath)
                    }
                else
                    FireStoreUtil.updateCurrentUser(et_name.text.toString(),et_bio.text.toString(),null)
                toast("Saving...")
            }
            btn_signout.setOnClickListener {
                AuthUI.getInstance().signOut(this.context!!).addOnCompleteListener { startActivity(intentFor<SignUpActivity>().newTask().clearTask())}
            }
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == RC_SELECT_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            val selectedImagePath = data.data
            val selectedImageBmp = MediaStore.Images.Media.getBitmap(activity?.contentResolver,selectedImagePath)

            val outputStream = ByteArrayOutputStream()
            selectedImageBmp.compress(Bitmap.CompressFormat.JPEG,90,outputStream)
            selectedImage = outputStream.toByteArray()
            GlideApp.with(this).load(selectedImage).into(iv_profile_image)
            pictureJustChanged = true
        }

    }

    override fun onStart() {
        super.onStart()
        FireStoreUtil.getCurrentUser {user ->
            if(this@MyAccountFragment.isVisible) {
                et_name.setText(user.name)
                et_bio.setText(user.bio)
                if(!pictureJustChanged && user.profileImagePath != null){
                    GlideApp.with(this).load(StorageUtil.getImagePath(user.profileImagePath)).into(iv_profile_image)
                }

            }
        }
    }

}
