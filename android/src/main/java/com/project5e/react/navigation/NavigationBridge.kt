package com.project5e.react.navigation

import com.facebook.react.bridge.*
import com.project5e.react.navigation.data.DestinationArgument
import com.project5e.react.navigation.data.bus.*
import com.project5e.react.navigation.data.react.RootWrapper
import com.project5e.react.navigation.utils.Mapper.toObj
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
        val root = data.toObj<RootWrapper>()?.root ?: throw NavigationException("root is null")
        if (root.bottomTabs == null && root.component == null) throw NavigationException("setRoot must be only one parameter")

        Store.dispatch(ACTION_REGISTER_REACT_COMPONENT, optionsCache)
        Store.dispatch(ACTION_SET_ROOT, root)
    }

    @ReactMethod
    fun currentRoute(promise: Promise) {
        Store.dispatch(ACTION_CURRENT_ROUTE, promise)
    }

    @ReactMethod
    fun setStyle(style: ReadableMap) {
    }

    @ReactMethod
    fun setTabBadge(data: ReadableArray) {
        Store.dispatch(ACTION_SET_TAB_BADGE, data)
    }

    @ReactMethod
    fun dispatch(screenID: String, action: String, component: String?, options: ReadableMap?, promise: Promise) {
        when (currentActivity) {
            is RnActivity -> {
                when (action) {
                    "push" -> Store.dispatch(
                        ACTION_DISPATCH_PUSH_NEST,
                        DestinationArgument(component, rnArgs = options)
                    )
                    "pop" -> Store.dispatch(ACTION_DISPATCH_POP_NEST)
                    else -> throw NavigationException("action(=$action) error")
                }
            }
            else -> {
                when (action) {
                    "push" -> Store.dispatch(ACTION_DISPATCH_PUSH, DestinationArgument(component, rnArgs = options))
                    "present" -> Store.dispatch(
                        ACTION_DISPATCH_PRESENT,
                        DestinationArgument(component, rnArgs = options)
                    )
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

}
