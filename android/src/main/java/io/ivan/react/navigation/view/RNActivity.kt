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
import io.ivan.react.navigation.R
import io.ivan.react.navigation.model.ARG_COMPONENT_NAME
import io.ivan.react.navigation.model.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.model.ARG_OPTIONS_SCREEN_ID
import io.ivan.react.navigation.model.Page
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel

open class RNActivity : RNBaseActivity() {

    open var mainComponentName: String = ""
    open var launchOptions: Bundle = Bundle()

    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: RNViewModel
            by lazy { createRNViewModel(application) }

    private val fragmentStateNavigator: FragmentStateNavigator
            by lazy { FragmentStateNavigator(this, navHostFragment.childFragmentManager, R.id.nav_host_fragment) }

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_nav_host)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController.navigatorProvider.addNavigator(fragmentStateNavigator)

        intent.extras?.apply {
            getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
            getBundle(ARG_LAUNCH_OPTIONS)?.let { launchOptions.putAll(it) }
        }
        launchOptions.getBundle(ARG_OPTIONS_SCREEN_ID) ?: launchOptions.putString(
            ARG_OPTIONS_SCREEN_ID,
            View.generateViewId().toString()
        )

        setupStartDestination()
        receiveDispatch()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navController.previousBackStackEntry?.apply { navController.navigateUp() } ?: finish()
    }

    private fun receiveDispatch() {
        Store.reducer(ACTION_DISPATCH_PUSH_NEST)?.observe(this, Observer { state ->
            val page = state as Page
            addDestinationAndNavigate(page)
        })
        Store.reducer(ACTION_DISPATCH_POP_NEST)?.observe(this, Observer {
            removeCurrentScreenIdToStack()
            navController.navigateUp()
        })
    }

    private fun setupStartDestination() {
        check(!TextUtils.isEmpty(mainComponentName)) { "Cannot loadApp if component name is null" }
        val startDestination = buildDestination(fragmentStateNavigator, mainComponentName, launchOptions)
        navController.setStartDestination(startDestination)
    }

    private fun addDestinationAndNavigate(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(anim_right_enter_right_exit) }
    ) {
        val destination = buildDestination(fragmentStateNavigator, page.rootName, Arguments.toBundle(page.options))
        navController.graph.addDestination(destination)
        navController.navigate(destination.id)
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