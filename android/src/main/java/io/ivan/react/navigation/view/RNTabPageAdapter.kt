package io.ivan.react.navigation.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import io.ivan.react.navigation.model.Tabs

class RNTabPageAdapter(fragment: Fragment, private val tabs: Tabs) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = tabs.pages.size

    override fun createFragment(position: Int): Fragment = RNFragment().apply {
        mainComponentName = tabs.pages[position].rootName
        launchOptions = Bundle().also {
            it.putString("screenID", "$id#$position")
        }
    }

}
