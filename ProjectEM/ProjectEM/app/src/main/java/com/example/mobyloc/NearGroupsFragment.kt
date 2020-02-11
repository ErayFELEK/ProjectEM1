package com.example.proje1


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mobyloc.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.group_row.view.*
import kotlinx.android.synthetic.main.user_row_new_message.view.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class NearGroupsFragment : Fragment() {
    lateinit var foundRecycleGroup: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root =  inflater.inflate(R.layout.fragment_near_groups, container, false)
        // Inflate the layout for this fragment
        foundRecycleGroup = root.findViewById<RecyclerView>(R.id.recycle_group)
        fetchGroups()
        Log.d("Group", "just before returning root")
        return root
    }

    companion object{
        val GROUP_KEY = "GROUP_KEY"
    }

    override fun onResume() {
        super.onResume()
        fetchGroups()
    }

    private fun fetchGroups() {
        val ref = FirebaseDatabase.getInstance().getReference("/groups")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()

                p0.children.forEach{
                    Log.d("Group" , it.toString())
                    val group = it.getValue(Group::class.java)
                    if(group != null){
                        Log.d("Group", "group name is ${group.groupName}")
                        adapter.add(GroupItem(group))
                        Log.d("Group", "after adding to adapter")
                    }

                }

                adapter.setOnItemClickListener { item, view ->
                    val groupItem = item as GroupItem
                    val incUserID = FirebaseAuth.getInstance().uid
                    val groupRef = FirebaseDatabase.getInstance()
                        .getReference("/groups/${groupItem.group.uid}/groupsuser").push()
                    Log.d("Group" , "current user is: $incUserID")

                    groupRef.setValue(incUserID)
                        .addOnSuccessListener {
                            Log.d("Group", "Saved message to database $groupRef")
                        }

                    val intent = Intent(view.context, GroupLogActivity::class.java)
                    intent.putExtra(GROUP_KEY, groupItem.group)
                    startActivity(intent)


                }

                foundRecycleGroup.adapter = adapter
                Log.d("Group", "after setting the adapter")
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


}

class GroupItem(val group: Group): Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        Log.d("Group", "before binding")
        viewHolder.itemView.group_name_group_row.text = group.groupName
        Log.d("Group", "binding completed")
    }

    override fun getLayout(): Int {
        Log.d("Group", "before returning layout")
        return R.layout.group_row
    }
}
