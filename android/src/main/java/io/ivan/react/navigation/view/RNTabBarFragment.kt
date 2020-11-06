package io.ivan.react.navigation.view

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.uimanager.PixelUtil
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RootViewModel

const val ARG_TAB_BAR_COMPONENT_NAME = "arg_tab_bar_component_name"

class RNTabBarFragment : Fragment() {

    private lateinit var viewModel: RootViewModel
    private lateinit var navHostFragment: NavHostFragment

    private val tabBarHeight = PixelUtil.toPixelFromDIP(56f).toInt()
    private val tabsId = mutableListOf<Int>()
    private var tabBarComponentName: String? = null

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(RootViewModel::class.java)
        arguments?.let {
            tabBarComponentName = it.getString(ARG_TAB_BAR_COMPONENT_NAME)
        }
    }

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

        viewModel.tabs?.pages?.first()?.let {
            val startDestination = buildDestination(it.rootName)
            navController.setStartDestination(startDestination)
            tabsId.add(startDestination.id)
        }
        viewModel.tabs?.pages?.listIterator(1)?.forEach {
            val destination = buildDestination(it.rootName)
            navController.graph.addDestinations(destination)
            tabsId.add(destination.id)
        }

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(requireActivity(), { state ->
            val data = state as ReadableMap
            val index = data.getInt("index")
            navController.navigate(tabsId[index])
        })
    }

    private fun createTabBarContainer(context: Context) =
        FrameLayout(context).apply {
            id = View.generateViewId()
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, tabBarHeight, Gravity.BOTTOM)
            val tabBarFragment = RNFragment().apply {
                mainComponentName = tabBarComponentName
                launchOptions = Bundle().also { bundle ->
                    bundle.putString("screenID", id.toString())
                }
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
