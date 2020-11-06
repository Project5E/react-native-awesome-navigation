package io.ivan.react.navigation.view

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.PixelUtil
import io.ivan.react.navigation.utils.*


class RNTabBarFragment : Fragment() {

    private lateinit var navHostFragment: NavHostFragment
    private val tabBarHeight = PixelUtil.toPixelFromDIP(56f).toInt()

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return with(inflater.context) {
            FrameLayout(this).also {
                it.addView(createTabBarContainer(this))
                it.addView(createContentContainer(this))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val startDestination = buildDestination("Home")
        val buildDestination = buildDestination("Setting")
        navController.setStartDestination(startDestination)
        navController.graph.addDestinations(buildDestination)

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), { state ->
            val data = state as ReadableMap
            val index = data.getInt("index")
            navController.navigate(if (index == 0) startDestination.id else buildDestination.id)
        })
    }

    private fun createTabBarContainer(context: Context) =
        FrameLayout(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, tabBarHeight, Gravity.BOTTOM)
            val tabBarFragment = RNFragment()
            tabBarFragment.mainComponentName = "TabBar"
            tabBarFragment.launchOptions = Bundle().also { bundle ->
                bundle.putString("screenID", "TabBar")
            }
            childFragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(id, tabBarFragment)
                .commitNowAllowingStateLoss()
        }

    private fun createContentContainer(context: Context) =
        FrameLayout(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                setMargins(0, 0, 0, tabBarHeight)

                navHostFragment = createNavHostFragmentWithoutGraph()

                childFragmentManager.beginTransaction()
                    .setPrimaryNavigationFragment(navHostFragment)
                    .add(id, navHostFragment)
                    .commitNowAllowingStateLoss()
            }
        }

    private fun buildDestination(destinationName: String): NavDestination {
        return buildDestination(requireContext(), childFragmentManager, destinationName)
    }

}
