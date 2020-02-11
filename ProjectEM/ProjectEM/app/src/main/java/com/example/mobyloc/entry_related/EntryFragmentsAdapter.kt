package com.example.mobyloc.entry_related

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

val mainContentPageTitles = arrayOf(
    "Recent",
    "Popular"
)

const val MAIN_CONTENT_PAGE_NUM = 2

class EntryFragmentsAdapter(private val context: Context, fm: FragmentManager) :FragmentPagerAdapter(fm){

    override fun getItem(position: Int): Fragment {

        return when(position){
            0-> RecentEntriesFragment()
            1-> PopularEntriesFragment()
            else-> RecentEntriesFragment()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mainContentPageTitles[position]
    }

    override fun getCount(): Int {
        return MAIN_CONTENT_PAGE_NUM
    }


}