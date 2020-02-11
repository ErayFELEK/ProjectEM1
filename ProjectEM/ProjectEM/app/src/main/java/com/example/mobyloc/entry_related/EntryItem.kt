package com.example.mobyloc.entry_related

import android.widget.TextView
import com.example.mobyloc.R
import com.google.firebase.database.*
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.entry_template.view.*

class EntryItem(var entry : Entry) : Item<GroupieViewHolder>(){

    override fun getLayout(): Int {
        return R.layout.entry_template
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.entry_textcontent).text = entry.content.textContent
        FirebaseDatabase.getInstance().getReference("/users").child(entry.owner_uid).child("username")
            .addListenerForSingleValueEvent(object :ValueEventListener{
                override fun onDataChange(p0: DataSnapshot) {
                    viewHolder.itemView.entry_username.text = p0.getValue(String::class.java).toString()
                }

                override fun onCancelled(p0: DatabaseError) {

                }

            })



    }


}