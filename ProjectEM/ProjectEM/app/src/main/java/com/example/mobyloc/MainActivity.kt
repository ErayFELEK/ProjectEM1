package com.example.mobyloc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    //private FirebaseAuth mFirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        register_button_register.setOnClickListener {
            performRegister()
            Handler().postDelayed(
                {
                    saveUserToDatabase()
                },
                4000 // value in milliseconds
            )

        }

        already_have_account_text_view.setOnClickListener {
            Log.d("MainActivity" , "try login" )
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

    }

    private fun saveUserToDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users")

        val user = User(uid, username_edittext_register.text.toString())

        ref.child(uid).setValue(user)
            .addOnSuccessListener {
                Log.d("Main" , "saved to database")

                val intent = Intent(this, LatestMessageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {
                Log.d("Main" , "database error: ${it.message}")
            }
    }

    private fun performRegister() {
        val email = email_edittext_register.text.toString()
        val password = password_edittext_register.text.toString()

        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "please enter email/pw" , Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("MainActivity" , "email is: " + email)
        Log.d("MainActivity" , "password is: $password" )

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener

                Log.d("Main" , "successfuly created user with uid: ${it.result?.user?.uid}")

            }
            .addOnFailureListener {
                Log.d("Main" , "Failed to create user: ${it.message}")
                Toast.makeText(this, "Failed to create user: ${it.message}" , Toast.LENGTH_SHORT).show()
            }
    }

}

@Parcelize
class User(val uid: String, val username: String): Parcelable {
    constructor() : this("" , "")
}


