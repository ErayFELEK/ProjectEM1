package com.example.proje1


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout

import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.example.mobyloc.CreateGroup
import com.example.mobyloc.R
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
class StartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val toolbar = getView()!!.findViewById<androidx.appcompat.widget.Toolbar>(R.id.chats_toolbar)
        (activity as AppCompatActivity).setSupportActionBar(toolbar)
        setHasOptionsMenu(true)

        val fab: FloatingActionButton = getView()!!.findViewById(R.id.fab)
        fab.setOnClickListener { view ->

            val intent = Intent(view.context, CreateGroup::class.java)
            startActivity(intent)

        }

        val gecici: FloatingActionButton = getView()!!.findViewById(R.id.gecici)
        gecici.setOnClickListener { view ->
            findNavController().navigate(R.id.action_startFragment_to_entriesOpeningFragment)

        }

        val drawerLayout: DrawerLayout = (activity as AppCompatActivity).findViewById(R.id.drawer_layout)
        val toggle = ActionBarDrawerToggle(
            activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        //settings for viewpager and tabs
        val pager = getView()!!.findViewById<ViewPager>(R.id.chats_viewpager)
        val tabs = getView()!!.findViewById<TabLayout>(R.id.chats_tab)
        pager.adapter = ChatFragmentsAdapter(activity!!, childFragmentManager)
        tabs.setupWithViewPager(pager)


    }
}
