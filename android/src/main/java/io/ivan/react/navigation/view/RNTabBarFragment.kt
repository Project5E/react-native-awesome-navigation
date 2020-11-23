package io.ivan.react.navigation.view

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.PixelUtil
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RootViewModel
import java.util.*

class RNTabBarFragment : Fragment() {

    private lateinit var view: ViewGroup
    private lateinit var viewPager: ViewPager2

    private val tabBarHeight = PixelUtil.toPixelFromDIP(56f).toInt()

    private val viewModel: RootViewModel by lazy { ViewModelProvider(requireActivity()).get(RootViewModel::class.java) }
    private val tabBarContainerId by lazy { View.generateViewId() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.beginTransaction()
            .add(tabBarContainerId, createTabBarFragment())
            .commitNowAllowingStateLoss()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (!this::view.isInitialized) {
            view = with(inflater.context) {
                FrameLayout(this).also {
                    it.addView(createTabBarContainer(this))
                    it.addView(createContentContainer(this))
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.tabs?.let { viewPager.adapter = RNTabPageAdapter(this, it) }
        viewPager.isUserInputEnabled = false
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val page = viewModel.tabs?.pages?.get(position)
                page?.let { setNavigationBarStyle(it.rootName) }
            }
        })

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), Observer { state ->
            val data = state as ReadableMap
            val index = data.optInt("index")
            viewPager.currentItem = index
        })
    }

    private fun createTabBarFragment(): RNFragment =
        RNFragment().apply {
            mainComponentName = viewModel.tabBarComponentName
            launchOptions = Bundle().also {
                it.putString("screenID", id.toString())
                it.putSerializable("tabs", pageOptionList())
            }
        }

    private fun createTabBarContainer(context: Context) =
        FrameLayout(context).apply {
            id = tabBarContainerId
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                tabBarHeight,
                Gravity.BOTTOM
            )
        }

    private fun createContentContainer(context: Context) =
        ViewPager2(context).apply {
            viewPager = this
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, 0, 0, tabBarHeight)
            }
        }

    private fun pageOptionList(): ArrayList<Bundle?> =
        (viewModel.tabs?.pages?.map { Arguments.toBundle(it.options) } ?: mutableListOf()) as ArrayList


    private fun setNavigationBarStyle(pageRootName: String) {
        val navigationOption = viewModel.navigationOptionCache[pageRootName]
        with(activity as AppCompatActivity) {
            when (navigationOption?.optBoolean("hideNavigationBar")) {
                true -> {
                    supportActionBar?.hide()
                }
                else -> {
                    supportActionBar?.show()
                    supportActionBar?.title = navigationOption?.optString("title") ?: ""
                }
            }
        }
    }

}
