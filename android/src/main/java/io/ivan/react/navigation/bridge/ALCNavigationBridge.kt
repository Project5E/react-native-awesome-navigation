package io.ivan.react.navigation.bridge

import android.os.Bundle
import android.util.Log
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavArgumentBuilder
import androidx.navigation.NavGraphNavigator
import androidx.navigation.fragment.FragmentNavigator
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.*
import io.ivan.react.navigation.R
import io.ivan.react.navigation.view.ARG_COMPONENT_NAME
import io.ivan.react.navigation.view.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.view.RNFragment
import io.ivan.react.navigation.view.RNRootActivity


class ALCNavigationBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "ALCNavigationBridge"
    }

    @ReactMethod
    fun setRoot(root: ReadableMap) {
        FLog.w("1van", "setRoot. ${root.toString()}")
        val currentActivity = currentActivity
        FLog.w("1van", "setRoot1 $currentActivity")
        if (currentActivity is RNRootActivity) {
            FLog.w("1van", "setRoot2")
            val startDestination =
                FragmentNavigator(
                    currentActivity,
                    currentActivity.supportFragmentManager,
                    R.id.content
                ).createDestination().also {
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
            FLog.w("1van", "setRoot3")
            val navController = currentActivity.navHostFragment.navController
            FLog.w("1van", "setRoot4")
            val graph = NavGraphNavigator(navController.navigatorProvider).createDestination().also {
                it.addDestination(startDestination)
                it.startDestination = startDestination.id
            }
            navController.graph = graph
        }
    }

    @ReactMethod
    fun signalFirstRenderComplete(screenID: String) {
        FLog.w("1van", screenID)
    }

    @ReactMethod
    fun registerReactComponent(appKey: String, options: ReadableMap) {
        FLog.w("1van", "registerReactComponent.")
    }

    @ReactMethod
    fun setStyle(style: ReadableMap) {
        FLog.w("1van", "setStyle.")
    }

    @ReactMethod
    fun setTabBadge(badge: ReadableArray) {
        FLog.w("1van", "setTabBadge.")
    }

    @ReactMethod
    fun dispatch(screenID: String, action: String, component: String?, options: ReadableMap?) {
        Log.d("1van", "$screenID $action $component ${options.toString()}")
        Log.d("1van", "dispatch $currentActivity")
        if (currentActivity is FragmentActivity) {
            val fragments = (currentActivity as FragmentActivity).supportFragmentManager.fragments
            val currentFragment = fragments.last()
            val args = Bundle().also { args ->
                args.putString("arg_component_name", component)
                args.putBundle("arg_launch_options", Bundle().also { options ->
                    options.putString("screenID", "Home")
                })
            }
//            currentFragment.findNavController().navigate(R.id.action_global_globalFragment, args)
        }
    }

    @ReactMethod
    fun currentRoute(promise: Promise) {

    }

}
