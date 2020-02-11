package com.example.mobyloc.entry_related


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager

import com.example.mobyloc.R
import com.example.proje1.ChatFragmentsAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 *
 */
class EntriesOpeningFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_entries_opening, container, false)

        val newEntryButton = root.findViewById<FloatingActionButton>(R.id.entry_send_floating)

        newEntryButton.setOnClickListener {
            findNavController().navigate(R.id.action_entriesOpeningFragment_to_writeNewEntryFragment)
        }


        //settings for viewpager and tabs
        val pager = root.findViewById<ViewPager>(R.id.entries_viewpager)
        val tabs = root.findViewById<TabLayout>(R.id.entries_tab)
        pager.adapter = EntryFragmentsAdapter(activity!!, childFragmentManager)
        tabs.setupWithViewPager(pager)

        return root
    }


}
