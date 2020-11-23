package io.ivan.react.navigation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.ivan.react.navigation.model.Tabs

class RNTabPageAdapter(fm: FragmentManager, private val tabs: Tabs) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getCount(): Int = tabs.pages.size

    override fun getItem(position: Int): Fragment = RNFragment().apply {
        mainComponentName = tabs.pages[position].rootName
        launchOptions = Bundle().also {
            it.putString("screenID", "$id#$position")
        }
    }

}
