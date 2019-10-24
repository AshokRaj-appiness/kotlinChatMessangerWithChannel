package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.myapplication.utils.FireStoreUtil
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.android.synthetic.main.activity_sign_up.*
import org.jetbrains.anko.*

class SignUpActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1
    private val signInProviders = listOf(AuthUI.IdpConfig.EmailBuilder().setAllowNewAccounts(true).setRequireName(true).build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_signIn.setOnClickListener {
            val intent = AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(signInProviders)
                        .setLogo(R.drawable.chat_logo)
                        .build()
            startActivityForResult(intent,RC_SIGN_IN)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            val response = IdpResponse.fromResultIntent(data)
            if(resultCode == Activity.RESULT_OK){
                val progressDialog = indeterminateProgressDialog("Setting up your account")
                FireStoreUtil.initCurrentUserFirstTime {
                    startActivity(intentFor<MainActivity>().newTask().clearTask())
                    progressDialog.dismiss()
                }
            }else if(resultCode == Activity.RESULT_CANCELED){
                if(response == null) return
                when(response.error?.errorCode){
                    ErrorCodes.NO_NETWORK -> toast("Network error")
                    ErrorCodes.UNKNOWN_ERROR -> toast("UnKnown Error")
                }
            }
        }
    }
}
