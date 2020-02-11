package com.example.proje1

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

val mainContentPageTitles = arrayOf(
    "NearestGroups",
    "Chats"
)

const val MAIN_CONTENT_PAGE_NUM = 2

class ChatFragmentsAdapter(private val context: Context, fm: FragmentManager) :FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {

        return when(position){
            0->NearGroupsFragment()
            1->ChatsFragment()
            else->ChatsFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mainContentPageTitles[position]
    }

    override fun getCount(): Int {
        return MAIN_CONTENT_PAGE_NUM
    }


}