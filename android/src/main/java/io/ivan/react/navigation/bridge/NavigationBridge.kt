package io.ivan.react.navigation.bridge

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.*
import io.ivan.react.navigation.utils.ACTION_SET_ROOT
import io.ivan.react.navigation.utils.Store
import io.ivan.react.navigation.utils.toJSONObject


class NavigationBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "ALCNavigationBridge"
    }

    @ReactMethod
    fun setRoot(root: ReadableMap) {
        currentActivity?.let {
            val startDestinationName = getStartDestinationName(root)
            Store.dispatch(ACTION_SET_ROOT, startDestinationName)
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

    private fun getStartDestinationName(root: ReadableMap): String? {
        val rootJson = root.toJSONObject().getJSONObject("root")
        val launchPage = rootJson.optJSONObject("screen")?.getString("moduleName")
        rootJson.optJSONObject("stack")?.getJSONObject("root")
        rootJson.optJSONObject("tabs")?.getJSONArray("children")
        return launchPage
    }

}
