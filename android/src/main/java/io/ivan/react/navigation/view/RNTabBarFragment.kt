package io.ivan.react.navigation.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.NavigationConstants.Companion.VIEW_DID_APPEAR
import io.ivan.react.navigation.NavigationConstants.Companion.VIEW_DID_DISAPPEAR
import io.ivan.react.navigation.NavigationEmitter.sendNavigationEvent
import io.ivan.react.navigation.R
import io.ivan.react.navigation.model.ARG_COMPONENT_NAME
import io.ivan.react.navigation.model.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.utils.ACTION_DISPATCH_SWITCH_TAB
import io.ivan.react.navigation.utils.Store
import io.ivan.react.navigation.utils.optInt
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel
import io.ivan.react.navigation.view.widget.SwipeControllableViewPager
import java.util.*

class RNTabBarFragment : Fragment(), RNComponentLifecycle {

    private lateinit var viewPager: SwipeControllableViewPager
    private val tabBarRnFragment: RNFragment by lazy { createTabBarRnFragment() }

    private val viewModel: RNViewModel by lazy { createRNViewModel(requireActivity().application) }

    private val currentScreenId: String
        get() = viewModel.screenIdStack[viewModel.currentTabIndex]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parent = inflater.inflate(R.layout.fragment_tabbar, container, false) as ViewGroup
        viewPager = parent.findViewById(R.id.view_pager)
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.tab_bar_container, tabBarRnFragment)
            .commitNowAllowingStateLoss()

        // swipe enable
        viewPager.isEnabled = false

        viewModel.tabs?.let { viewPager.adapter = RNTabPageAdapter(childFragmentManager, it) }

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), Observer { state ->
            val data = state as ReadableMap
            val index = data.optInt("index")
            viewModel.currentTabIndex = index
            viewPager.setCurrentItem(index, false)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (view as ViewGroup).removeAllViews()
    }

    override fun viewDidAppear() {
        tabBarRnFragment.viewDidAppear()
        sendNavigationEvent(VIEW_DID_APPEAR, currentScreenId)
    }

    override fun viewDidDisappear() {
        tabBarRnFragment.viewDidDisappear()
        sendNavigationEvent(VIEW_DID_DISAPPEAR, currentScreenId)
    }

    private fun createTabBarRnFragment(): RNFragment =
        RNFragment().apply {
            arguments = Bundle().also { args ->
                args.putString(ARG_COMPONENT_NAME, viewModel.tabBarComponentName)
                args.putBundle(ARG_LAUNCH_OPTIONS, Bundle().also { options ->
                    options.putSerializable("tabs", pageOptionList())
                })
            }
        }

    private fun pageOptionList(): ArrayList<Bundle?> =
        (viewModel.tabs?.pages?.map { Arguments.toBundle(it.options) } ?: mutableListOf()) as ArrayList

}
