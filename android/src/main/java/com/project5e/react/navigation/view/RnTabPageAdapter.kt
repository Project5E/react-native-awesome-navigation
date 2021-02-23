package com.project5e.react.navigation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.project5e.react.navigation.data.ARG_COMPONENT_NAME
import com.project5e.react.navigation.data.Tabs

class RnTabPageAdapter(fm: FragmentManager, private val tabs: Tabs) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = tabs.pages.size

    override fun getItem(position: Int): Fragment = RnFragment().apply {
        arguments = Bundle().apply {
            putString(ARG_COMPONENT_NAME, tabs.pages[position].rootName)
        }
    }

}
