package com.example.mobyloc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        login_button_login.setOnClickListener {
            val email = email_edittext_login.text.toString()
            val password = password_edittext_login.text.toString()

            Log.d("Login", "Attempt login with email/pw: $email/***")

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if(!it.isSuccessful) return@addOnCompleteListener
                    Log.d("Login" , "successfuly logged in user with uid: ${it.result?.user?.uid}")

                    val intent = Intent(this, LatestMessageActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Log.d("Login" , "Failed to log in user: ${it.message}")
                }
        }

        back_to_register_textview.setOnClickListener{
            finish()
        }
    }

}