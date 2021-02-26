package com.project5e.react.navigation.view

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.project5e.react.navigation.NavigationException
import com.project5e.react.navigation.R
import com.project5e.react.navigation.data.ARG_COMPONENT_NAME
import com.project5e.react.navigation.data.ARG_LAUNCH_OPTIONS
import com.project5e.react.navigation.data.ARG_OPTIONS_SCREEN_ID
import com.project5e.react.navigation.data.Page
import com.project5e.react.navigation.data.bus.ACTION_DISPATCH_POP_NEST
import com.project5e.react.navigation.data.bus.ACTION_DISPATCH_PUSH_NEST
import com.project5e.react.navigation.data.bus.Store
import com.project5e.react.navigation.utils.DestinationManager
import com.project5e.react.navigation.utils.setGraphWithStartDestination
import com.project5e.react.navigation.view.model.RnViewModel
import com.project5e.react.navigation.view.model.createRnViewModel

open class RnActivity : RnBaseActivity() {

    val navController: NavController get() = navHostFragment.navController
    val dm: DestinationManager by lazy { DestinationManager(this, navController) }

    val screenId get() = _screenId
    open var launchOptions: Bundle = Bundle()

    private var _mainComponentName: String = ""
    private var _screenId: String? = null
    private lateinit var navHostFragment: NavHostFragment

    private val viewModel: RnViewModel by lazy { createRnViewModel(application) }

    override fun getMainComponentName(): String {
        return _mainComponentName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_nav_host)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        intent.extras?.apply {
            getString(ARG_COMPONENT_NAME)?.let { _mainComponentName = it }
            getBundle(ARG_LAUNCH_OPTIONS)?.let { launchOptions.putAll(it) }
        }
        launchOptions.getBundle(ARG_OPTIONS_SCREEN_ID) ?: launchOptions.putString(
            ARG_OPTIONS_SCREEN_ID,
            View.generateViewId().toString()
        )

        setupNavGraph()
        receiveDispatch()
    }

    override fun invokeDefaultOnBackPressed() {
        navController.navigateUp()
    }

//    override fun onBackPressed() {
//        if (!navController.navigateUp()) {
//            finish()
//        }
//    }

    private fun receiveDispatch() {
        Store.reducer(ACTION_DISPATCH_PUSH_NEST)?.observe(this, { state ->
            val page = state as Page
            dm.push(page)
        })
        Store.reducer(ACTION_DISPATCH_POP_NEST)?.observe(this, {
            removeCurrentScreenIdToStack()
            onBackPressed()
        })
    }

    private fun setupNavGraph() {
        check(!TextUtils.isEmpty(mainComponentName)) { "Cannot loadApp if component name is null" }
        val startDestination = dm.createRnFragmentDestination(mainComponentName, launchOptions)
        navController.setGraphWithStartDestination(startDestination)
    }

    private fun removeCurrentScreenIdToStack() {
        navController.currentDestination?.id?.toString()?.let {
            if (!viewModel.screenIdStack.contains(it)) {
                throw NavigationException("currentDestinationId(id = $it) is not found in ScreenIdStack")
            }
            viewModel.screenIdStack.remove(it)
        }
    }

}
