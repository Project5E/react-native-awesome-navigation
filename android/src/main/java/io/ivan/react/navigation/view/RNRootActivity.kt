package io.ivan.react.navigation.view

import android.os.Bundle
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.R
import io.ivan.react.navigation.bridge.NavigationConstants
import io.ivan.react.navigation.utils.*


class RNRootActivity : RNBaseActivity() {

    private lateinit var navHostFragment: NavHostFragment
    private var startDestination: NavDestination? = null

    private val navController: NavController
        get() = navHostFragment.navController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navHostFragment = NavHostFragment.create(0)

        supportFragmentManager.beginTransaction()
            .add(Window.ID_ANDROID_CONTENT, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commit()

        receive()
    }

    private fun receive() {
        Store.reducer(ACTION_REGISTER_REACT_COMPONENT)?.observe(this, { state ->
            val pair = state as Pair<String, ReadableMap>
        })

        Store.reducer(ACTION_SET_ROOT)?.observe(this, { state ->
            val startDestinationName = state as String
            startDestination = buildStartDestination(startDestinationName)
            setStartDestination(startDestination)
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
                Arguments.createMap().also { it.merge(data) },
                NavigationConstants.RESULT_TYPE_OK
            )
        })

        dispatch()
    }

    private fun dispatch() {
        Store.reducer(ACTION_DISPATCH_PUSH)?.observe(this, { state ->
            val data = state as Pair<String, ReadableMap>
            val destinationName = data.first
            val destination = buildDestination(destinationName)
            navController.graph.addDestination(destination)
            navController.navigate(destination.id)
        })

        Store.reducer(ACTION_DISPATCH_POP)?.observe(this, {
            navController.popBackStack()
        })

        Store.reducer(ACTION_DISPATCH_POP_TO_ROOT)?.observe(this, {
            startDestination?.let {
                navController.navigate(it.id)
            }
        })

        Store.reducer(ACTION_DISPATCH_PRESENT)?.observe(this, { state ->
            val data = state as Pair<String, ReadableMap>
            val destinationName = data.first
            val destination = buildDestination(destinationName)
            navController.graph.addDestination(destination)
            val navOptionsBuilder = navOptions {
                anim {
                    enter = R.anim.navigation_top_enter
                    exit = android.R.anim.fade_out
                    popEnter = android.R.anim.fade_in
                    popExit = R.anim.navigation_top_exit
                }
            }
            navController.navigate(destination.id, null, navOptionsBuilder)
        })

        Store.reducer(ACTION_DISPATCH_DISMISS)?.observe(this, {
            navController.popBackStack()
        })

        Store.reducer(ACTION_DISPATCH_SWITCH_TAB)?.observe(this, {

        })
    }

    private fun buildStartDestination(startDestinationName: String): NavDestination {
        return FragmentNavigator(this, supportFragmentManager, R.id.content).createDestination().also {
            val viewId = ViewCompat.generateViewId()
            it.id = viewId
            it.className = RNFragment::class.java.name
            it.addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().let { arg ->
                arg.defaultValue = startDestinationName
                arg.build()
            })
            it.addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().let { arg ->
                arg.defaultValue = Bundle().also { bundle -> bundle.putString("screenID", viewId.toString()) }
                arg.build()
            })
        }
    }

    private fun setStartDestination(startDestination: NavDestination?) {
        startDestination ?: return

        val graph = NavGraphNavigator(navController.navigatorProvider).createDestination().also {
            it.addDestination(startDestination)
            it.startDestination = startDestination.id
        }
        navController.graph = graph
    }

    private fun buildDestination(destinationName: String): NavDestination {
        return FragmentNavigator(this, supportFragmentManager, R.id.content).createDestination().also {
            val viewId = ViewCompat.generateViewId()
            it.id = viewId
            it.className = RNFragment::class.java.name
            it.addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().let { arg ->
                arg.defaultValue = destinationName
                arg.build()
            })
            it.addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().let { arg ->
                arg.defaultValue = Bundle().also { bundle -> bundle.putString("screenID", viewId.toString()) }
                arg.build()
            })
        }
    }

}
