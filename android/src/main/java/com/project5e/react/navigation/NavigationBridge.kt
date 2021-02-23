package com.project5e.react.navigation

import com.facebook.react.bridge.*
import com.project5e.react.navigation.data.*
import com.project5e.react.navigation.data.bus.*
import com.project5e.react.navigation.utils.optArray
import com.project5e.react.navigation.utils.optMap
import com.project5e.react.navigation.utils.optString
import com.project5e.react.navigation.view.RnActivity

class NavigationBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private val optionsCache: MutableMap<String, ReadableMap?> = mutableMapOf()

    override fun getName(): String {
        return "ALCNavigationBridge"
    }

    @ReactMethod
    fun registerReactComponent(componentName: String, componentOptions: ReadableMap) {
        optionsCache[componentName] = componentOptions
    }

    @ReactMethod
    fun setRoot(data: ReadableMap) {
        Store.dispatch(ACTION_REGISTER_REACT_COMPONENT, optionsCache)

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
    }

    @ReactMethod
    fun setTabBadge(badge: ReadableArray) {
    }

    @ReactMethod
    fun dispatch(screenID: String, action: String, component: String?, options: ReadableMap?, promise: Promise) {
        when (currentActivity) {
            is RnActivity -> {
                when (action) {
                    "push" -> Store.dispatch(ACTION_DISPATCH_PUSH_NEST, requirePage(component, options))
                    "pop" -> Store.dispatch(ACTION_DISPATCH_POP_NEST)
                    else -> throw NavigationException("action(=$action) error")
                }
            }
            else -> {
                when (action) {
                    "push" -> Store.dispatch(ACTION_DISPATCH_PUSH, requirePage(component, options))
                    "present" -> Store.dispatch(ACTION_DISPATCH_PRESENT, requirePage(component, options))
                    "popToRoot" -> Store.dispatch(ACTION_DISPATCH_POP_TO_ROOT)
                    "pop" -> Store.dispatch(ACTION_DISPATCH_POP)
                    "dismiss" -> Store.dispatch(ACTION_DISPATCH_DISMISS, promise)
                    "popPages" -> Store.dispatch(ACTION_DISPATCH_POP_PAGES, options)
                    "switchTab" -> Store.dispatch(ACTION_DISPATCH_SWITCH_TAB, options)
                    else -> throw NavigationException("action(=$action) error")
                }
            }
        }
    }


    @ReactMethod
    fun setResult(data: ReadableMap) {
        Store.dispatch(ACTION_SET_RESULT, data)
    }

    @ReactMethod
    fun signalFirstRenderComplete(screenID: String) {
    }

    private fun parseRoot(root: ReadableMap?): Root? {
        val rootMap = root?.optMap("root")?.takeIf { it.toHashMap().size > 0 }
            ?: throw NavigationException("setRoot must be only one parameter")
        return with(rootMap) {
            when {
                hasKey("tabs") -> {
                    parseTabs(this).let { Tabs(RootType.TABS, it!!.first, it.second) }
                }
                hasKey("stack") -> {
                    parseStack(this)?.let { Screen(RootType.STACK, it) }
                }
                hasKey("screen") -> {
                    parseNameInScreen(this)?.let { Screen(RootType.SCREEN, it) }
                }
                else -> throw NavigationException("setRoot parameter error")
            }
        }
    }

    private fun parseNameInScreen(root: ReadableMap?): Page? {
        return root?.optMap("screen")?.optString("moduleName")?.let {
            Page(it)
        }
    }

    private fun parseStack(root: ReadableMap?): Page? {
        return root?.optMap("stack")?.let {
            parseRootInStack(it)
        }
    }

    private fun parseRootInStack(stack: ReadableMap?): Page? {
        val root = stack?.optMap("root")
        val options = stack?.optMap("options")
        return parseNameInScreen(root)?.copy(options = options)
    }

    private fun parseTabs(root: ReadableMap?): Pair<List<Page>, ReadableMap?>? {
        val tabs = root?.optMap("tabs")
        val stacks = tabs?.optArray("children")
        val options = tabs?.optMap("options")
        stacks ?: throw NavigationException("setRoot parameter error, children is undefined")

        val pages = mutableListOf<Page>()
        for (index in 0 until stacks.size()) {
            val stack = stacks.getMap(index)?.optMap("stack")
            parseRootInStack(stack)?.let {
                pages.add(it)
            }
        }
        return pages to options
    }

    private fun requirePage(component: String?, options: ReadableMap?) =
        component?.let { Page(it, options) } ?: throw NavigationException("componentName is null")

}
