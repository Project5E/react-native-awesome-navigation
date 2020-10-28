package io.ivan.react.navigation.bridge

import android.util.Log
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.*
import io.ivan.react.navigation.utils.*


class NavigationBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "ALCNavigationBridge"
    }

    @ReactMethod
    fun setRoot(root: ReadableMap) {
        Log.d("1van", "setRoot currentActivity = $currentActivity")
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
        Log.d("1van", "dispatch currentActivity = $currentActivity")
        currentActivity?.let {
            when (action) {
                "push" -> Store.dispatch(ACTION_DISPATCH_PUSH, component to options)
                "pop" -> Store.dispatch(ACTION_DISPATCH_POP)
                "popToRoot" -> Store.dispatch(ACTION_DISPATCH_POP_TO_ROOT)
                "present" -> Store.dispatch(ACTION_DISPATCH_PRESENT, component to options)
                "dismiss" -> Store.dispatch(ACTION_DISPATCH_DISMISS)
                "switchTab" -> Store.dispatch(ACTION_DISPATCH_SWITCH_TAB, component to options)
                else -> throw Exception("action error")
            }
        }
    }

    @ReactMethod
    fun currentRoute(promise: Promise) {
        Log.d("1van", "currentRoute currentActivity = $currentActivity")
    }

    @ReactMethod
    fun setResult(data: ReadableMap) {
        Log.d("1van", "setResult currentActivity = $currentActivity")
    }

    private fun getStartDestinationName(root: ReadableMap): String? {
        val rootJson = root.toJSONObject().getJSONObject("root")
        val launchPage = rootJson.optJSONObject("screen")?.getString("moduleName")
        rootJson.optJSONObject("stack")?.getJSONObject("root")
        rootJson.optJSONObject("tabs")?.getJSONArray("children")
        return launchPage
    }

}
