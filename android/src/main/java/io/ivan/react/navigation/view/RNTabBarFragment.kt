package io.ivan.react.navigation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.R
import io.ivan.react.navigation.utils.ACTION_DISPATCH_SWITCH_TAB
import io.ivan.react.navigation.utils.Store
import io.ivan.react.navigation.utils.optInt
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel
import io.ivan.react.navigation.view.widget.SwipeControllableViewPager
import java.util.*

class RNTabBarFragment : Fragment() {

    private val viewModel: RNViewModel by lazy { createRNViewModel(requireActivity().application) }

    private lateinit var viewPager: SwipeControllableViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parent = inflater.inflate(R.layout.fragment_tabbar, container, false) as ViewGroup
        viewPager = parent.findViewById(R.id.view_pager)
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.tab_bar_container, createTabBarFragment())
            .commitNowAllowingStateLoss()

        // swipe enable
        viewPager.isEnabled = false

        viewModel.tabs?.let { viewPager.adapter = RNTabPageAdapter(childFragmentManager, it) }

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), Observer { state ->
            val data = state as ReadableMap
            val index = data.optInt("index")
            viewPager.setCurrentItem(index, false)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewPager.clearOnPageChangeListeners()
        (view as ViewGroup).removeAllViews()
    }

    private fun createTabBarFragment(): RNFragment =
        RNFragment().apply {
            mainComponentName = viewModel.tabBarComponentName
            launchOptions = Bundle().also {
                it.putSerializable("tabs", pageOptionList())
            }
        }

    private fun pageOptionList(): ArrayList<Bundle?> =
        (viewModel.tabs?.pages?.map { Arguments.toBundle(it.options) } ?: mutableListOf()) as ArrayList

}
