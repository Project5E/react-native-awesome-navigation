package io.ivan.react.navigation.view

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.NavigationConstants
import io.ivan.react.navigation.R
import io.ivan.react.navigation.model.*
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel

open class RNRootActivity : RNBaseActivity() {

    private var startDestination: NavDestination? = null

    private val viewModel: RNViewModel by lazy { createRNViewModel(application) }
    private val navHostFragment: NavHostFragment by lazy { createNavHostFragmentWithoutGraph() }

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.container, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commit()

        receive()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.previousBackStackEntry?.apply { navController.navigateUp() }
            ?: startActivity(Intent(Intent.ACTION_MAIN).also {
                it.addCategory(Intent.CATEGORY_HOME)
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }

    private fun receive() {
        Store.reducer(ACTION_REGISTER_REACT_COMPONENT)?.observe(this, Observer { state ->
            viewModel.navigationOptionCache.putAll(state as MutableMap<String, ReadableMap?>)
        })

        Store.reducer(ACTION_SET_ROOT)?.observe(this, Observer { state ->
            with(state as Root) {
                when {
                    this is Tabs && type == RootType.TABS -> {
                        viewModel.tabs = this
                        options?.optString("tabBarModuleName")?.let {
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
        })

        Store.reducer(ACTION_CURRENT_ROUTE)?.observe(this, Observer { state ->
            val promise = state as Promise
            promise.resolve(navController.currentDestination?.id)
        })

        Store.reducer(ACTION_SET_RESULT)?.observe(this, Observer { state ->
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
        Store.reducer(ACTION_DISPATCH_PUSH)?.observe(this, Observer { state ->
            val page = state as Page
            addDestinationAndNavigate(page)
        })

        Store.reducer(ACTION_DISPATCH_PRESENT)?.observe(this, Observer { state ->
            val page = state as Page
            addDestinationAndNavigate(page, null, navOptions {
                anim(anim_bottom_enter_bottom_exit)
            })
        })

        Store.reducer(ACTION_DISPATCH_POP_TO_ROOT)?.observe(this, Observer {
            startDestination?.let {
                navController.navigate(it.id)
            }
        })

        Store.reducer(ACTION_DISPATCH_POP)?.observe(this, Observer {
            navController.navigateUp()
        })

        Store.reducer(ACTION_DISPATCH_DISMISS)?.observe(this, Observer {
            navController.navigateUp()
        })

        Store.reducer(ACTION_DISPATCH_POP_PAGES)?.observe(this, Observer { state ->
            val data = state as ReadableMap
            val count = data.optInt("count")
            for (i in 0 until count) {
                navController.navigateUp()
            }
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

    private fun addDestinationAndNavigate(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(anim_right_enter_right_exit) }
    ) {
        val destination = buildDestination(page)
        navController.graph.addDestination(destination)
        navController.navigate(destination.id, args, navOptions)
    }

}
