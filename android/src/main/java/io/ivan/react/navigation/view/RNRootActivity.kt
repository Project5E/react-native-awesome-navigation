package io.ivan.react.navigation.view

import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavController
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.R
import io.ivan.react.navigation.utils.ACTION_DISPATCH_POP
import io.ivan.react.navigation.utils.ACTION_DISPATCH_PUSH
import io.ivan.react.navigation.utils.ACTION_SET_ROOT
import io.ivan.react.navigation.utils.Store


class RNRootActivity : RNBaseActivity() {

    private lateinit var navHostFragment: NavHostFragment

    private val navController: NavController
        get() = navHostFragment.navController

    private var testId = 0

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
        Store.reducer(ACTION_SET_ROOT)?.observe(this, {
            val startDestinationName = it as String
            val startDestination = buildStartDestination(startDestinationName)
            setStartDestination(startDestination)
        })

        Store.reducer(ACTION_DISPATCH_PUSH)?.observe(this, {
            val data = it as Pair<String, ReadableMap>
            val destinationName = data.first
            val destination = buildDestination(destinationName)
            navController.graph.addDestination(destination)
            Log.d("1van testId", testId.toString())
            navHostFragment.navController.navigate(testId)
        })

        Store.reducer(ACTION_DISPATCH_POP)?.observe(this, {
            navHostFragment.navController.popBackStack()
        })
    }

    private fun buildStartDestination(startDestinationName: String): FragmentNavigator.Destination {
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

    private fun setStartDestination(startDestination: FragmentNavigator.Destination) {
        val graph = NavGraphNavigator(navController.navigatorProvider).createDestination().also {
            it.addDestination(startDestination)
            it.startDestination = startDestination.id
        }
        navController.graph = graph
    }

    private fun buildDestination(destinationName: String): FragmentNavigator.Destination {
        return FragmentNavigator(this, supportFragmentManager, R.id.content).createDestination().also {
            val viewId = ViewCompat.generateViewId()
            testId = viewId
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
