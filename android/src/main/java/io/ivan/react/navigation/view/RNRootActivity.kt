package io.ivan.react.navigation.view

import android.os.Bundle
import android.view.Window
import androidx.core.view.ViewCompat
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import io.ivan.react.navigation.R
import io.ivan.react.navigation.utils.ACTION_SET_ROOT
import io.ivan.react.navigation.utils.Store


class RNRootActivity : RNBaseActivity() {

    private lateinit var navHostFragment: NavHostFragment

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
            val startDestination = buildStartDestination(it as String)
            setStartDestination(startDestination)
        })
    }

    private fun buildStartDestination(
        startDestinationName: Any?
    ): FragmentNavigator.Destination {
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
        val navController = navHostFragment.navController
        val graph = NavGraphNavigator(navController.navigatorProvider).createDestination().also {
            it.addDestination(startDestination)
            it.startDestination = startDestination.id
        }
        navController.graph = graph
    }

}
