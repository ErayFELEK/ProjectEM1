package com.example.proje1


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobyloc.ChatLogActivity
import com.example.mobyloc.R
import com.example.mobyloc.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.*
import kotlinx.android.synthetic.main.fragment_chats.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class ChatsFragment : Fragment() {
    lateinit var foundRecycleChat: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root =  inflater.inflate(R.layout.fragment_chats, container, false)

        //val adapter = GroupAdapter<GroupieViewHolder>()

        //adapter.add(UserItem())
        //adapter.add(UserItem())
        //adapter.add(UserItem())

        foundRecycleChat = root.findViewById<RecyclerView>(R.id.recycle_chat)

        //foundRecycleChat.adapter = adapter

        fetchUsers()

        return root

    }

    companion object{
        val USER_KEY = "USER_KEY"
    }

    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach{
                    Log.d("Chats" , it.toString())
                    val user = it.getValue(User::class.java)
                    if(user != null){
                        adapter.add(UserItem(user))
                    }

                }

                adapter.setOnItemClickListener { item, view ->
                    val UserItem = item as UserItem

                    val intent = Intent(view.context, ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY, UserItem.user)
                    startActivity(intent)


                }

                foundRecycleChat.adapter = adapter
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })

    }

}

class UserItem(val user: User): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.username_user_row_chat.text = user.username
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_message
    }
}