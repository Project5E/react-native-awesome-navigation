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
    fun registerReactComponent(componentName: String, componentOptions: ReadableMap) {
        FLog.w("1van", "registerReactComponent. $componentName $componentOptions")
        Store.dispatch(ACTION_REGISTER_REACT_COMPONENT, componentName to componentOptions)
    }

    @ReactMethod
    fun setRoot(root: ReadableMap) {
        Log.d("1van", "setRoot currentActivity = $currentActivity")
        getStartDestinationName(root)?.let { startDestinationName ->
            Store.dispatch(ACTION_SET_ROOT, startDestinationName)
        }
    }

    @ReactMethod
    fun currentRoute(promise: Promise) {
        Store.dispatch(ACTION_CURRENT_ROUTE, promise)
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

    @ReactMethod
    fun setResult(data: ReadableMap) {
        Store.dispatch(ACTION_SET_RESULT, data)
    }

    @ReactMethod
    fun signalFirstRenderComplete(screenID: String) {
        FLog.w("1van signalFirstRenderComplete", screenID)
    }

    private fun getStartDestinationName(root: ReadableMap?): String? {
        val rootJson = root?.toJSONObject()?.getJSONObject("root")
        val launchPage = rootJson?.optJSONObject("screen")?.getString("moduleName")
        rootJson?.optJSONObject("stack")?.getJSONObject("root")
        rootJson?.optJSONObject("tabs")?.getJSONArray("children")
        return launchPage
    }

}
