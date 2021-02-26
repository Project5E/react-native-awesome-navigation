package com.project5e.react.navigation.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import com.project5e.react.navigation.NavigationConstants.Companion.COMPONENT_RESULT
import com.project5e.react.navigation.NavigationEmitter.sendNavigationEvent
import com.project5e.react.navigation.NavigationException
import com.project5e.react.navigation.NavigationManager.clearInstanceManagerInHost
import com.project5e.react.navigation.NavigationManager.registerRnDestination
import com.project5e.react.navigation.NavigationManager.resetInstanceManagerInHost
import com.project5e.react.navigation.NavigationManager.style
import com.project5e.react.navigation.R
import com.project5e.react.navigation.data.*
import com.project5e.react.navigation.data.bus.*
import com.project5e.react.navigation.navigator.RnFragmentNavigator
import com.project5e.react.navigation.utils.*
import com.project5e.react.navigation.view.model.RnViewModel
import com.project5e.react.navigation.view.model.createRnViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

open class RnRootActivity : RnBaseActivity() {

    val navController: NavController get() = navHostFragment.navController
    val destinationManager: DestinationManager by lazy { DestinationManager(this, navController) }

    private var isTabBarPresented: Boolean = false
    private lateinit var navHostFragment: NavHostFragment
    private val viewModel: RnViewModel by lazy { createRnViewModel(application) }
    private val backStack: Deque<NavBackStackEntry> @SuppressLint("RestrictedApi") get() = navController.backStack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_nav_host)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        with(navController.navigatorProvider) {
            addNavigator(createRnPushFragmentNavigator(navHostFragment))
            addNavigator(createRnPresentFragmentNavigator(navHostFragment))
            addNavigator(createRnFragmentNavigator(this))
        }
        receive()
        if (savedInstanceState != null) {
            viewModel.cacheNavGraph?.let { navController.graph = it }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        viewModel.cacheNavGraph = navController.graph
    }

    override fun onBackPressed() {
        val currentDestination = navController.currentDestination
        var isClearInstanceManagerInHost = false
        if (currentDestination is FragmentNavigator.Destination) {
            val fragmentClass = classLoader.loadClass(currentDestination.className)
            if (!RnFragment::class.java.isAssignableFrom(fragmentClass)) {
                clearInstanceManagerInHost()
                isClearInstanceManagerInHost = true
            }
        }
        super.onBackPressed()
        if (isClearInstanceManagerInHost) {
            resetInstanceManagerInHost()
        }
    }

    override fun invokeDefaultOnBackPressed() {
        val currentDestinationId = getCurrentDestinationId()

        destinationManager.popBackType = RnFragmentNavigator.PopBackType.POP
        if (navController.navigateUp()) {
            viewModel.screenIdStack.remove(currentDestinationId.toString())
        }
    }

    private fun receive() {
        Store.reducer(ACTION_REGISTER_REACT_COMPONENT)?.observe(this, { state ->
            val data = state as MutableMap<String, ReadableMap?>

            data.keys.forEach { registerRnDestination(it) }
            viewModel.navigationOptionCache.putAll(data)
        })

        Store.reducer(ACTION_SET_ROOT)?.observe(this, { state ->
            with(state as Root) {
                when {
                    this is Tabs && type == RootType.TABS -> {
                        viewModel.tabs = this
                        viewModel.tabBarComponentName = options?.optString("tabBarModuleName")
                    }
                    this is Screen && type == RootType.STACK -> {
                        viewModel.page = page
                    }
                    this is Screen && type == RootType.SCREEN -> {
                        viewModel.page = page
                    }
                }
            }
            buildStartDestination()?.apply { navController.setGraphWithStartDestination(this) }
        })

        Store.reducer(ACTION_CURRENT_ROUTE)?.observe(this, { state ->
            val promise = state as Promise
            promise.resolve(Arguments.createMap().also {
                it.putString(ARG_OPTIONS_SCREEN_ID, getCurrentScreenId())
            })
        })

        Store.reducer(ACTION_SET_RESULT)?.observe(this, { state ->
            val data = state as ReadableMap
            viewModel.pageResult = data
        })

        receiveDispatch()
    }

    private fun receiveDispatch() {
        Store.reducer(ACTION_DISPATCH_PUSH)?.observe(this, { state ->
            destinationManager.push(state as Page)
        })

        Store.reducer(ACTION_DISPATCH_PRESENT)?.observe(this, { state ->
            val page = state as Page
            val animated = page.options?.optBoolean("animated") ?: false
            isTabBarPresented = page.options?.optBoolean("isTabBarPresented") ?: false
            destinationManager.present(page, null, if (animated) navOptions { anim(style.presentAnim) } else null)
        })

        Store.reducer(ACTION_DISPATCH_POP_TO_ROOT)?.observe(this, {
            navController.graph.startDestination.let {
                clearStack()
                destinationManager.navigationType = RnFragmentNavigator.NavigationType.PUSH
                navController.navigate(it)
            }
        })

        Store.reducer(ACTION_DISPATCH_POP)?.observe(this, {
            val currentDestinationId = getCurrentDestinationId()

            destinationManager.popBackType = RnFragmentNavigator.PopBackType.POP
            if (navController.navigateUp()) {
                viewModel.screenIdStack.remove(currentDestinationId.toString())
                sendComponentResultEvent()
            }
        })

        Store.reducer(ACTION_DISPATCH_DISMISS)?.observe(this, { state ->
            val promise = state as Promise
            // 在执行 navigateUp() 前，计算需要出栈的次数
            val popBackSize = destinationManager.lastPresentId?.let { id ->
                val subIndex = backStack.map { it.destination.id }.indexOf(id)
                backStack.size - subIndex
            }

            destinationManager.popBackType = RnFragmentNavigator.PopBackType.DISMISS
            if (navController.navigateUp()) {
                // navigateUp() 方法内会先对 backStack 出栈
                // 所以 screenIdStack 也先执行一次 removeLast()
                viewModel.screenIdStack.removeLast()
                popBackSize?.let {
                    for (index in 1 until it) {
                        backStack.removeLast()
                        viewModel.screenIdStack.removeLast()
                    }
                }

                lifecycleScope.launch {
                    withContext(Dispatchers.IO) {
                        val dismissAnimTime = resources.getInteger(android.R.integer.config_mediumAnimTime).toLong()
                        delay(dismissAnimTime)
                        promise.resolve(null)
                        sendComponentResultEvent()
                    }
                }
            }
        })

        Store.reducer(ACTION_DISPATCH_POP_PAGES)?.observe(this, { state ->
            val data = state as ReadableMap
            val count = data.optInt("count")

            for (i in 0 until count) {
                val currentDestinationId = getCurrentDestinationId()
                destinationManager.popBackType = RnFragmentNavigator.PopBackType.POP
                if (navController.navigateUp()) {
                    viewModel.screenIdStack.remove(currentDestinationId.toString())
                }
            }
        })
    }

    private fun buildStartDestination(): NavDestination? = buildTabsDestination() ?: buildPageDestination()

    private fun buildTabsDestination(): NavDestination? =
        viewModel.tabs?.run {
            destinationManager.navigator.createDestination().also {
                it.id = ViewCompat.generateViewId()
                it.className = RnTabBarFragment::class.java.name
            }
        }

    private fun buildPageDestination(): NavDestination? =
        viewModel.page?.run { destinationManager.createDestination(this) }

    private fun clearStack() {
        backStack.removeAll { it != backStack.first }

        viewModel.screenIdStack =
            viewModel.tabs?.pages?.size
                // with tab bar
                ?.let { it -> viewModel.screenIdStack.subList(0, it) }
                    // without tab bar
                ?: viewModel.screenIdStack.subList(0, 1)

    }

    private fun getCurrentDestinationId(): Int? {
        return navController.currentDestination?.id?.let {
            if (!viewModel.screenIdStack.contains(it.toString()) && navController.graph.startDestination != it) {
                throw NavigationException("currentDestinationId(id = $it) is not found in ScreenIdStack")
            }
            it
        }
    }

    private fun getCurrentScreenId(): String {
        return viewModel.tabs?.pages?.size
            // with tab bar
            ?.let { tabSize ->
                if (viewModel.screenIdStack.size == tabSize) {
                    viewModel.screenIdStack[viewModel.currentTabIndex]
                } else {
                    viewModel.screenIdStack.last()
                }
            }
        // without tab bar
            ?: let {
                viewModel.screenIdStack.last()
            }
    }

    private fun getScreenId(index: Int): String {
        return viewModel.tabs?.pages?.size
            // with tab bar
            ?.let { tabSize ->
                if (viewModel.screenIdStack.size == tabSize) {
                    viewModel.screenIdStack[viewModel.currentTabIndex]
                } else {
                    viewModel.screenIdStack[index]
                }
            }
        // without tab bar
            ?: let {
                viewModel.screenIdStack[index]
            }
    }

    private fun sendComponentResultEvent() {
        viewModel.pageResult?.let {
            sendNavigationEvent(COMPONENT_RESULT, getCurrentScreenId(), Arguments.createMap().apply { merge(it) })
        }
        if (isWithRnTabBar() && isTabBarPresented) {
            viewModel.pageResult?.let {
                sendNavigationEvent(
                    COMPONENT_RESULT,
                    viewModel.tabBarScreenId,
                    Arguments.createMap().apply { merge(it) })
            }
        }
        viewModel.pageResult = null
    }

    private fun isWithRnTabBar(): Boolean {
        return viewModel.tabs != null && viewModel.tabBarScreenId != null
    }

}
