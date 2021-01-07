package io.ivan.react.navigation.view

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.navOptions
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.NavigationConstants
import io.ivan.react.navigation.NavigationEmitter
import io.ivan.react.navigation.R
import io.ivan.react.navigation.model.ARG_COMPONENT_NAME
import io.ivan.react.navigation.model.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.model.ARG_OPTIONS_SCREEN_ID
import io.ivan.react.navigation.model.Page
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel

open class RNActivity : RNBaseActivity() {

    open var launchOptions: Bundle = Bundle()
    private var _mainComponentName: String = ""

    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: RNViewModel
            by lazy { createRNViewModel(application) }

    private val rnNavigator: RNFragmentNavigator
            by lazy { createRNFragmentNavigator(navHostFragment) }

    private val navController: NavController
        get() = navHostFragment.navController

    override fun getMainComponentName(): String {
        return _mainComponentName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_nav_host)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController.navigatorProvider.addNavigator(rnNavigator)

        intent.extras?.apply {
            getString(ARG_COMPONENT_NAME)?.let { _mainComponentName = it }
            getBundle(ARG_LAUNCH_OPTIONS)?.let { launchOptions.putAll(it) }
        }
        launchOptions.getBundle(ARG_OPTIONS_SCREEN_ID) ?: launchOptions.putString(
            ARG_OPTIONS_SCREEN_ID,
            View.generateViewId().toString()
        )

        setupNavGraph()
        receive()
    }

    override fun onBackPressed() {
        if (!navController.navigateUp()) {
            finish()
        }
    }

    private fun receive() {
        Store.reducer(ACTION_DISPATCH_PUSH_NEST)?.observe(this, Observer { state ->
            val (page, promise) = state as Pair<Page, Promise>
            addDestinationAndNavigate(page)
            viewModel.prevPageResult?.let { promise.resolve(Arguments.createMap().apply { merge(it) }) }
        })
        Store.reducer(ACTION_DISPATCH_POP_NEST)?.observe(this, Observer {
            removeCurrentScreenIdToStack()
            onBackPressed()
        })
        Store.reducer(ACTION_SET_RESULT_NEST)?.observe(this, Observer { state ->
            val data = state as ReadableMap
            viewModel.prevPageResult = data
            NavigationEmitter.sendNavigationEvent(
                NavigationConstants.COMPONENT_RESULT,
                navController.previousBackStackEntry?.destination?.id?.toString(),
                Arguments.createMap().apply { merge(data) }
            )
        })
    }

    private fun setupNavGraph() {
        check(!TextUtils.isEmpty(mainComponentName)) { "Cannot loadApp if component name is null" }
        val startDestination = buildDestination(rnNavigator, mainComponentName, launchOptions)
        navController.setGraph(startDestination)
    }

    private fun addDestinationAndNavigate(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(anim_right_enter_right_exit) }
    ) {
        val destination = buildDestination(rnNavigator, page.rootName, Arguments.toBundle(page.options))
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

}
