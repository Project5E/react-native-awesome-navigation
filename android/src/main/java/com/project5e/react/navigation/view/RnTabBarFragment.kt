package com.project5e.react.navigation.view

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem.SHOW_AS_ACTION_ALWAYS
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project5e.react.navigation.R
import com.project5e.react.navigation.data.ARG_COMPONENT_NAME
import com.project5e.react.navigation.data.ARG_LAUNCH_OPTIONS
import com.project5e.react.navigation.data.TabBadge
import com.project5e.react.navigation.data.bus.ACTION_DISPATCH_SWITCH_TAB
import com.project5e.react.navigation.data.bus.ACTION_SET_TAB_BADGE
import com.project5e.react.navigation.data.bus.Store
import com.project5e.react.navigation.utils.*
import com.project5e.react.navigation.view.model.RnViewModel
import com.project5e.react.navigation.view.model.createRnViewModel
import com.project5e.react.navigation.view.widget.SwipeControllableViewPager
import java.util.*

class RnTabBarFragment : Fragment(), RnComponentLifecycle {

    private lateinit var viewPager: SwipeControllableViewPager
    private lateinit var rnTabBar: FragmentContainerView
    private lateinit var tabBar: BottomNavigationView

    private var tabBarRnFragment: RnFragment? = null

    private val viewModel: RnViewModel by lazy { createRnViewModel(requireActivity().application) }

    // 判断tabBar是否Rn组件
    private val isRnTabBar: Boolean
        get() = viewModel.tabBarComponentName != null

    // 当前Tab的ScreenId
    private val currentTabScreenId: String
        get() = viewModel.screenIdStack[viewModel.currentTabIndex]

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parent = inflater.inflate(R.layout.fragment_tabbar, container, false) as ViewGroup
        viewPager = parent.findViewById(R.id.view_pager)
        rnTabBar = parent.findViewById(R.id.rn_tab_bar)
        tabBar = parent.findViewById(R.id.tab_bar)
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTabBar()
        setupViewPager()
        receive()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (view as ViewGroup).removeAllViews()
    }

    override fun viewDidAppear() {
        tabBarRnFragment?.viewDidAppear()
        sendViewAppearEvent(activity ?: this, currentTabScreenId, true)
    }

    override fun viewDidDisappear() {
        tabBarRnFragment?.viewDidDisappear()
        sendViewAppearEvent(activity ?: this, currentTabScreenId, false)
    }

    private fun receive() {
        if (isRnTabBar) {
            Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), { state ->
                val data = state as ReadableMap
                val index = data.optInt("index") ?: 0
                viewModel.currentTabIndex = index
                viewPager.setCurrentItem(index, false)
            })
        } else {
            Store.reducer(ACTION_SET_TAB_BADGE)?.observe(requireActivity()) { state ->
                val badgeArray = state as ReadableArray? ?: return@observe

                repeat(badgeArray.size()) {
                    badgeArray.getMap(it)?.apply { TabBadge(this).bindTabBar(tabBar) }
                }
            }
        }
    }

    private fun setupTabBar() {
        if (isRnTabBar) {
            rnTabBar.visibility = View.VISIBLE
            tabBarRnFragment = createTabBarRnFragment().also {
                childFragmentManager.beginTransaction()
                    .replace(rnTabBar.id, it)
                    .commitNowAllowingStateLoss()
            }
        } else {
            tabBar.visibility = View.VISIBLE
            viewModel.tabs?.pages?.forEachIndexed { index, page ->
                val icon = page.options?.optMap("icon")
                val imageSource = getImageSource(requireContext(), icon)
                imageSource.load(requireContext(), {
                    it ?: return@load
                    tabBar.menu.add(Menu.NONE, Menu.NONE, index, page.rootName)
                        .setIcon(BitmapDrawable(requireContext().resources, it))
                        .setShowAsAction(SHOW_AS_ACTION_ALWAYS)
                })
            }
            tabBar.setOnNavigationItemSelectedListener {
                val index = it.order
                viewModel.currentTabIndex = index
                viewPager.setCurrentItem(index, false)
                return@setOnNavigationItemSelectedListener true
            }
        }
    }

    private fun setupViewPager() {
        viewPager.isEnabled = false // swipe enable
        viewModel.tabs?.let {
            viewPager.adapter = RnTabPageAdapter(childFragmentManager, it)
            viewPager.offscreenPageLimit = it.pages.size
        }
    }

    private fun createTabBarRnFragment(): RnFragment =
        RnFragment().apply {
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
