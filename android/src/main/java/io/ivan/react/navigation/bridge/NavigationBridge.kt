package io.ivan.react.navigation.bridge

import android.util.Log
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.*
import io.ivan.react.navigation.model.*
import io.ivan.react.navigation.utils.*
import org.json.JSONObject


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
    fun setRoot(data: ReadableMap) {
        parseRoot(data)?.let { root ->
            Store.dispatch(ACTION_SET_ROOT, root)
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
            "switchTab" -> Store.dispatch(ACTION_DISPATCH_SWITCH_TAB, options)
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

    private fun parseRoot(root: ReadableMap?): Root? {
        val rootJson = root?.toJSONObject()?.getJSONObject("root")
        rootJson?.takeIf { it.length() == 1 } ?: throw Exception("setRoot must be only one parameter")
        return with(rootJson) {
            when {
                has("tabs") -> {
                    parseTabs(this).let { Tabs(RootType.TABS, it!!.first, it.second) }
                }
                has("stack") -> {
                    parseStack(this)?.let { Screen(RootType.STACK, it) }
                }
                has("screen") -> {
                    parseScreen(this)?.let { Screen(RootType.SCREEN, it) }
                }
                else -> throw Exception("setRoot parameter error")
            }
        }
    }

    private fun parseScreen(root: JSONObject?): Page? {
        return root?.optJSONObject("screen")?.optString("moduleName")?.let {
            Page(it)
        }
    }

    private fun parseStack(root: JSONObject?): Page? {
        val stack = root?.optJSONObject("stack")
        val rootChildren = stack?.optJSONObject("root")
        val options = stack?.optJSONObject("options")
        return parseScreen(rootChildren)?.copy(options = options)
    }

    private fun parseTabs(root: JSONObject?): Pair<List<Page>, JSONObject?>? {
        val tabs = root?.optJSONObject("tabs")
        val stacks = tabs?.optJSONArray("children")
        val options = tabs?.optJSONObject("options")
        stacks ?: return null

        val pages = mutableListOf<Page>()
        for (index in 0 until stacks.length()) {
            val stack = stacks.getJSONObject(index)
            val rootChildren = stack?.optJSONObject("root")
            val optionsChildren = stack?.optJSONObject("options")
            parseScreen(rootChildren)?.copy(options = optionsChildren)?.let {
                pages.add(it)
            }
        }
        return pages to options
    }

}
