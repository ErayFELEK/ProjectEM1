package com.example.mobyloc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.example.proje1.ChatsFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*
import java.sql.Timestamp

class ChatLogActivity : AppCompatActivity() {
    companion object{
        val TAG = "ChatLog"
    }

    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

        recyclerview_chat_log.adapter = adapter

        //val username = intent.getStringExtra(ChatsFragment.USER_KEY)
        val user = intent.getParcelableExtra<User>(ChatsFragment.USER_KEY)
        supportActionBar?.title = user.username

        listenForMessages()


        send_button_chat_log.setOnClickListener {
            Log.d(TAG,"attemp to send message")
            performSendMessage()
        }
    }

    private fun listenForMessages() {
        val user = intent.getParcelableExtra<User>(ChatsFragment.USER_KEY)
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")

        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)
                if(chatMessage != null){
                    Log.d(TAG, chatMessage?.text)
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        adapter.add(ChatFromItem(chatMessage.text))
                    }
                    else{
                        adapter.add(ChatToItem(chatMessage.text))
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

    class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String,
                      val timestamp: Long) {
        constructor() : this("" ,"", "" , "" ,-1)
    }

    private fun performSendMessage() {
        val user = intent.getParcelableExtra<User>(ChatsFragment.USER_KEY)
        val text = edittext_chat_log.text.toString()
        val fromId = FirebaseAuth.getInstance().uid
        val toId = user.uid
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        if(fromId == null) return

        val chatMessage = ChatMessage(reference.key!!,text, fromId , toId, System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                Log.d(TAG, "Saved message to database ${reference.key}")
                edittext_chat_log.text.clear()
                recyclerview_chat_log.scrollToPosition(adapter.itemCount - 1)
            }

        toReference.setValue(chatMessage)

    }
}

class ChatFromItem(val text: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView2.text = text
    }
}

class ChatToItem(val text: String): Item<GroupieViewHolder>() {
    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.chat_to_text.text = text
    }
}
