package com.example.mobyloc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_create_group.*

class CreateGroup : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        create_button.setOnClickListener {
            performCreate()
        }
    }

    private fun performCreate(){
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/groups").push()

        val groupName = group_name.text.toString()
        val groupPassword = group_password.text.toString()
        if(fromId == null) return
        if(groupPassword == ""){
            Log.d("Group", "no password")
        }

        val groupClass = Group(reference.key!!, fromId, groupPassword, groupName)
        reference.setValue(groupClass)
            .addOnSuccessListener {
                Log.d("Group", "Saved message to database ${reference.key}")
            }

    }
}

@Parcelize
class Group(val uid: String, val adminId: String, val groupPassword: String, val groupName: String):Parcelable{
    constructor() :  this("", "", "", "")
}