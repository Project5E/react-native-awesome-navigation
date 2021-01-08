package io.ivan.react.navigation.view

import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.NavigationConstants.Companion.COMPONENT_RESULT
import io.ivan.react.navigation.NavigationEmitter.sendNavigationEvent
import io.ivan.react.navigation.R
import io.ivan.react.navigation.model.*
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class RNRootActivity : RNBaseActivity() {

    private var startDestination: NavDestination? = null

    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: RNViewModel
            by lazy { createRNViewModel(application) }

    private val dismissAnimTime: Long
            by lazy { resources.getInteger(android.R.integer.config_mediumAnimTime).toLong() }

    private val rnNavigator: RNFragmentNavigator
            by lazy { createRNFragmentNavigator(navHostFragment) }

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_nav_host)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController.navigatorProvider.addNavigator(rnNavigator)
        receive()
    }

    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            startActivity(Intent(Intent.ACTION_MAIN).also {
                it.addCategory(Intent.CATEGORY_HOME)
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
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
            navController.setGraph(startDestination)
        })

        Store.reducer(ACTION_CURRENT_ROUTE)?.observe(this, Observer { state ->
            val promise = state as Promise
            promise.resolve(Arguments.createMap().also {
                it.putString(ARG_OPTIONS_SCREEN_ID, getCurrentScreenId())
            })
        })

        Store.reducer(ACTION_SET_RESULT)?.observe(this, Observer { state ->
            val data = state as ReadableMap
            viewModel.prevPageResult = data
        })

        receiveDispatch()
    }

    private fun receiveDispatch() {
        Store.reducer(ACTION_DISPATCH_PUSH)?.observe(this, Observer { state ->
            val page = state as Page
            addDestinationAndNavigate(page)
        })

        Store.reducer(ACTION_DISPATCH_PRESENT)?.observe(this, Observer { state ->
            val page = state as Page
            val isTransparency = page.options?.getBoolean("isTransparency")
            addDestinationAndNavigate(
                page,
                Bundle().also { it.putBoolean(ARG_NAV_PENETRABLE, isTransparency ?: false) },
                navOptions { anim(anim_bottom_enter_bottom_exit) }
            )
        })

        Store.reducer(ACTION_DISPATCH_POP_TO_ROOT)?.observe(this, Observer {
            startDestination?.let {
                removeScreenIdStackWithNavigateToStartDestination()
                navController.navigate(it.id)
            }
        })

        Store.reducer(ACTION_DISPATCH_POP)?.observe(this, Observer {
            removeCurrentScreenIdToStack()
            navController.navigateUp()
            viewModel.prevPageResult?.let {
                sendNavigationEvent(
                    COMPONENT_RESULT,
                    getCurrentScreenId(),
                    Arguments.createMap().apply { merge(it) }
                )
            }
        })

        Store.reducer(ACTION_DISPATCH_DISMISS)?.observe(this, Observer { state ->
            val promise = state as Promise

            removeCurrentScreenIdToStack()
            navController.navigateUp()

            viewModel.prevPageResult?.let {
                sendNavigationEvent(
                    COMPONENT_RESULT,
                    getCurrentScreenId(),
                    Arguments.createMap().apply { merge(it) }
                )
            }

            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    delay(dismissAnimTime)
                    promise.resolve(null)
                }
            }
        })

        Store.reducer(ACTION_DISPATCH_POP_PAGES)?.observe(this, Observer { state ->
            val data = state as ReadableMap
            val count = data.optInt("count")
            for (i in 0 until count) {
                removeCurrentScreenIdToStack()
                navController.navigateUp()
            }
        })
    }

    private fun buildDestination(page: Page): NavDestination =
        buildDestination(rnNavigator, page.rootName, Arguments.toBundle(page.options))

    private fun buildDestinationWithTabBar(tabBarComponentName: String): NavDestination =
        rnNavigator.createDestination().also {
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

    private fun removeCurrentScreenIdToStack() {
        navController.currentDestination?.id?.toString()?.let {
            if (!viewModel.screenIdStack.contains(it)) {
                throw RNNavigationException("currentDestinationId(id = $it) is not found in ScreenIdStack")
            }
            viewModel.screenIdStack.remove(it)
        }
    }

    private fun removeScreenIdStackWithNavigateToStartDestination() {
        viewModel.screenIdStack =
            viewModel.tabs?.pages?.size
                // with tab bar
                ?.let { it -> viewModel.screenIdStack.subList(0, it) }
                    // without tab bar
                ?: viewModel.screenIdStack.subList(0, 1)

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

}
