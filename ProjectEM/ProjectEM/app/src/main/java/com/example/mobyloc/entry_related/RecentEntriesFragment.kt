package com.example.mobyloc.entry_related


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.xwray.groupie.GroupieViewHolder
import com.example.mobyloc.R
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class RecentEntriesFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_recent_entries, container, false)

        //setting the recycler view
        val recyclerView = root.findViewById<RecyclerView>(R.id.recent_entries_recycler)
        val groupAdapter = GroupAdapter<GroupieViewHolder>()

        recyclerView.adapter = groupAdapter

        val childEventListener = object : ChildEventListener{
            override fun onChildAdded(dataSnapshot: DataSnapshot, s: String?) {
                val new_entry = dataSnapshot.getValue(Entry::class.java)
                val new_entry_item = EntryItem(new_entry!!)

                (recyclerView.adapter as GroupAdapter<GroupieViewHolder>).add(new_entry_item)
            }

            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, s : String?) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }
        }

        FirebaseDatabase.getInstance().getReference("/entries").addChildEventListener(childEventListener)

        return root
    }


}

