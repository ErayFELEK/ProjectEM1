package com.example.mobyloc.entry_related


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.navigation.fragment.findNavController
import com.example.mobyloc.LatestMessageActivity

import com.example.mobyloc.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class WriteNewEntryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_write_new_entry, container, false)

        //send button
        val sendEntryButton = root.findViewById<Button>(R.id.send_entry)
        sendEntryButton.setOnClickListener {
            //create entry object to send
            val newEntry = Entry()
            //setting the content
            newEntry.content = EntryContent()
            newEntry.content.textContent = root.findViewById<AppCompatEditText>(R.id.new_entry_text_input).text.toString()

            //setting the owner of this entry
            newEntry.owner_uid = FirebaseAuth.getInstance().uid ?: "unknown"

            //send operations
            val new_ref = FirebaseDatabase.getInstance().getReference("/entries").push() //create referance for new entry
            val new_entry_id = new_ref.key  // get the key of the new entry

            new_ref.setValue(newEntry)
                .addOnSuccessListener {
                    Log.d("Entry" , "saved to database")
                    //add a referance for the new entry in the owner's "entries" section
                    FirebaseDatabase.getInstance().getReference("/users/" + newEntry.owner_uid + "/entries").push().setValue(new_entry_id)
                    activity!!.onBackPressed() // exit from this fragment
                }
                .addOnFailureListener {
                    Log.d("Entry" , "database error: ${it.message}")
                }
        }

        return root
    }


}
