package io.ivan.react.navigation.view

import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import androidx.navigation.ui.setupWithNavController
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.R
import io.ivan.react.navigation.bridge.NavigationConstants
import io.ivan.react.navigation.model.*
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RootViewModel

class RNRootActivity : RNBaseActivity() {

    private var startDestination: NavDestination? = null
    private lateinit var toolbar: Toolbar

    private val viewModel: RootViewModel by lazy { ViewModelProvider(this).get(RootViewModel::class.java) }
    private val navHostFragment: NavHostFragment by lazy { createNavHostFragmentWithoutGraph() }
    private val contextContainerId by lazy { View.generateViewId() }

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView()
        receive()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.navigateUp()
    }

    private fun setContentView() {
        supportFragmentManager.beginTransaction()
            .add(contextContainerId, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commit()
        setContentView(LinearLayout(this).also {
            it.orientation = LinearLayout.VERTICAL
            it.addView(createToolbar())
            it.addView(createNavHostFragmentContainer())
        })
        setSupportActionBar(toolbar)
    }

    private fun receive() {
        Store.reducer(ACTION_REGISTER_REACT_COMPONENT)?.observe(this, { state ->
            val pair = state as Pair<String, ReadableMap>
        })

        Store.reducer(ACTION_SET_ROOT)?.observe(this, { state ->
            with(state as Root) {
                when {
                    this is Tabs && type == RootType.TABS -> {
                        viewModel.tabs = this
                        options?.getString("tabBarModuleName")?.let {
                            startDestination = buildDestinationWithTabBar(it)
                        }
                        // TODO: 2020/11/6 如果没有 tabBarModuleName ，还应该处理使用原生 tabBar 的情况
                    }
                    this is Screen && type == RootType.STACK -> {
                        startDestination = buildDestination(page)
                    }
                    this is Screen && type == RootType.SCREEN -> {
                        startDestination = buildDestination(page)
                    }
                    else -> {
                    }
                }
            }
            navController.setStartDestination(startDestination)
            toolbar.setupWithNavController(navController)
        })

        Store.reducer(ACTION_CURRENT_ROUTE)?.observe(this, { state ->
            val promise = state as Promise
            promise.resolve(navController.currentDestination?.id)
        })

        Store.reducer(ACTION_SET_RESULT)?.observe(this, { state ->
            val data = state as ReadableMap

            sendNavigationEvent(
                NavigationConstants.COMPONENT_RESULT,
                navController.previousBackStackEntry?.destination?.id?.toString(),
                Arguments.createMap().also { it.merge(data) }
            )
        })

        dispatch()
    }

    private fun dispatch() {
        Store.reducer(ACTION_DISPATCH_PUSH)?.observe(this, { state ->
            val page = state as Page
            val destination = buildDestination(page)
            navController.graph.addDestination(destination)
            navController.navigate(destination.id, null, navOptions {
                anim {
                    enter = R.anim.navigation_slide_in_right
                    popExit = R.anim.navigation_slide_out_right
                }
            })
        })

        Store.reducer(ACTION_DISPATCH_POP)?.observe(this, {
            navController.navigateUp()
        })

        Store.reducer(ACTION_DISPATCH_POP_PAGES)?.observe(this, { state ->
            val data = state as ReadableMap
            val count = data.getInt("count")
            for (i in 0 until count) {
                navController.navigateUp()
            }
        })

        Store.reducer(ACTION_DISPATCH_POP_TO_ROOT)?.observe(this, {
            startDestination?.let {
                navController.navigate(it.id)
            }
        })

        Store.reducer(ACTION_DISPATCH_PRESENT)?.observe(this, { state ->
            val page = state as Page
            val destination = buildDestination(page)
            navController.graph.addDestination(destination)
            navController.navigate(destination.id, null, navOptions {
                anim {
                    enter = R.anim.navigation_top_enter
                    exit = android.R.anim.fade_out
                    popEnter = android.R.anim.fade_in
                    popExit = R.anim.navigation_top_exit
                }
            })
        })

        Store.reducer(ACTION_DISPATCH_DISMISS)?.observe(this, {
            navController.navigateUp()
        })
    }

    private fun buildDestination(page: Page): NavDestination =
        buildDestination(this, supportFragmentManager, page.rootName, Arguments.toBundle(page.options))

    private fun buildDestinationWithTabBar(tabBarComponentName: String): NavDestination =
        FragmentNavigator(this, supportFragmentManager, R.id.content).createDestination().also {
            it.id = ViewCompat.generateViewId()
            it.className = RNTabBarFragment::class.java.name
            viewModel.tabBarComponentName = tabBarComponentName
        }

    private fun getActionBarHeight(): Int =
        TypedValue().let {
            return if (theme.resolveAttribute(android.R.attr.actionBarSize, it, true)) {
                TypedValue.complexToDimensionPixelSize(it.data, Resources.getSystem().displayMetrics)
            } else 0
        }

    private fun createNavHostFragmentContainer() =
        FragmentContainerView(this).apply {
            id = contextContainerId
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

    private fun createToolbar() =
        Toolbar(this).apply {
            toolbar = this
            layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, getActionBarHeight())
        }

}
