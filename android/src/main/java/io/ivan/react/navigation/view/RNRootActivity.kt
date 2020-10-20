package io.ivan.react.navigation.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import com.facebook.react.ReactApplication
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import io.ivan.react.navigation.R

class RNRootActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler {

    private val reactNativeHost
        get() = (application as ReactApplication).reactNativeHost

    lateinit var navHostFragment: NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        navHostFragment = NavHostFragment.create(0)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, navHostFragment)
            .setPrimaryNavigationFragment(navHostFragment)
            .commit()
        reactNativeHost.reactInstanceManager.createReactContextInBackground()
    }

    override fun onStart() {
        super.onStart()
//        setStartDestination()
    }

    private fun setStartDestination() {
        val startDestination =
            FragmentNavigator(this, supportFragmentManager, R.id.content).createDestination().also {
                val viewId = ViewCompat.generateViewId()
                it.id = viewId
                it.className = RNFragment::class.java.name
                it.addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().let { arg ->
                    arg.defaultValue = "Home"
                    arg.build()
                })
                it.addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().let { arg ->
                    arg.defaultValue = Bundle().also { bundle -> bundle.putString("screenID", viewId.toString()) }
                    arg.build()
                })
            }
        val navController = navHostFragment.navController
        val graph = NavGraphNavigator(navController.navigatorProvider).createDestination().also {
            it.addDestination(startDestination)
            it.startDestination = startDestination.id
        }
        navController.graph = graph
    }

    //    override fun onResume() {
//        super.onResume()
//        if (reactNativeHost.hasInstance()) {
//            reactNativeHost.reactInstanceManager.onHostResume(this, this)
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (reactNativeHost.hasInstance()) {
//            reactNativeHost.reactInstanceManager.onHostPause(this)
//        }
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        if (reactNativeHost.hasInstance()) {
//            reactNativeHost.reactInstanceManager.onHostDestroy(this)
//        }
//    }
//
    override fun invokeDefaultOnBackPressed() {
    }

}
