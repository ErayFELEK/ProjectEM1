package com.example.mobyloc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.proje1.NearGroupsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_group_log.*

class GroupLogActivity : AppCompatActivity() {

    companion object{
        val TAG = "Group"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_log)

        recyclerview_group_log.adapter = adapter

        val group = intent.getParcelableExtra<Group>(NearGroupsFragment.GROUP_KEY)
        supportActionBar?.title = group.groupName

        listenForMessages()

        send_button_group_log.setOnClickListener {
            Log.d(TAG,"attemp to send message")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val group = intent.getParcelableExtra<Group>(NearGroupsFragment.GROUP_KEY)
        val text = edittext_group_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("/groups/${group.uid}/group-messages")

        ref.addChildEventListener(object: ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val groupMessage = p0.getValue(GroupMessage::class.java)
                if(groupMessage != null){
                    Log.d(TAG, groupMessage?.text)
                    if(groupMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(groupMessage.text))
                    }
                    else{
                        adapter.add(ChatToItem(groupMessage.text))
                    }
                }

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    private fun performSendMessage() {
        val group = intent.getParcelableExtra<Group>(NearGroupsFragment.GROUP_KEY)
        val text = edittext_group_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val reference = FirebaseDatabase.getInstance().getReference("/groups/${group.uid}/group-messages").push()
        if(fromId == null) return

        val groupMessage = GroupMessage(reference.key!!,text, fromId , System.currentTimeMillis()/1000)
        reference.setValue(groupMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved message to database ${reference.key}")
                edittext_group_log.text.clear()
                recyclerview_group_log.scrollToPosition(adapter.itemCount - 1)
            }


    }

    class GroupMessage(val id: String, val text: String, val fromId: String, val timestamp: Long) {
        constructor() : this("" ,"", ""  ,-1)
    }

}
