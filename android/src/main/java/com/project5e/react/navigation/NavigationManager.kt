package com.project5e.react.navigation

import android.annotation.SuppressLint
import android.app.Activity
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.ReactContext
import com.project5e.react.navigation.utils.LogFragment
import com.project5e.react.navigation.view.RnFragment
import com.project5e.react.navigation.view.model.GlobalStyle
import java.lang.ref.WeakReference

object NavigationManager {

    private var _reactNativeHost: ReactNativeHost? = null

    @SuppressLint("StaticFieldLeak")
    private var _reactInstanceManager: ReactInstanceManager? = null

    @SuppressLint("StaticFieldLeak")
    internal lateinit var cacheReactInstanceManager: ReactInstanceManager

    internal val registeredDestination: MutableMap<String, Class<out Any?>> = mutableMapOf()

    @JvmStatic
    @Volatile
    var style: GlobalStyle = GlobalStyle()

    val reactNativeHost: ReactNativeHost
        @JvmStatic
        get() = requireReactNativeHost()

    val reactInstanceManager: ReactInstanceManager
        @JvmStatic
        get() = requireReactNativeHost().reactInstanceManager

    @JvmStatic
    var TAG: String? = null
        set(value) {
            LogFragment.TAG = value
            field = value
        }

    @JvmStatic
    fun install(reactNativeHost: ReactNativeHost) {
        _reactNativeHost = reactNativeHost
        _reactInstanceManager = reactInstanceManager
        cacheReactInstanceManager = reactInstanceManager
        setup()
    }

    private fun setup() {
        reactInstanceManager.addReactInstanceEventListener {
            // TODO: 12/15/20  
        }
        if (!reactInstanceManager.hasStartedCreatingInitialContext()) {
            reactInstanceManager.createReactContextInBackground()
        }
    }

    @JvmStatic
    fun <T> register(key: String, value: Class<T>) {
        registeredDestination[key] = value
    }

    internal fun registerRnDestination(key: String) {
        register(key, RnFragment::class.java)
    }

    @JvmStatic
    fun requireReactNativeHost(): ReactNativeHost =
        _reactNativeHost ?: throw NavigationException("must call NavigationManager#install first")

    @JvmStatic
    fun clearInstanceManagerInHost() {
        val reactNativeHostClass = ReactNativeHost::class.java
        val mReactInstanceManagerField = reactNativeHostClass.getDeclaredField("mReactInstanceManager")
        mReactInstanceManagerField.isAccessible = true
        _reactInstanceManager = mReactInstanceManagerField.get(reactNativeHost) as? ReactInstanceManager?
        mReactInstanceManagerField.set(reactNativeHost, null)
    }

    @JvmStatic
    fun resetInstanceManagerInHost() {
        val reactNativeHostClass = ReactNativeHost::class.java
        val mReactInstanceManagerField = reactNativeHostClass.getDeclaredField("mReactInstanceManager")
        mReactInstanceManagerField.isAccessible = true
        mReactInstanceManagerField.set(reactNativeHost, _reactInstanceManager)
    }

    @JvmStatic
    fun resetCurrentActivity(activity: Activity) {
        resetCurrentInManager(activity)
        resetCurrentInContext(activity)
    }

    private fun resetCurrentInManager(activity: Activity) {
        val reactInstanceManagerClass = ReactInstanceManager::class.java
        val mCurrentActivityField = reactInstanceManagerClass.getDeclaredField("mCurrentActivity")
        mCurrentActivityField.isAccessible = true
        mCurrentActivityField.set(reactInstanceManager, activity)
    }

    private fun resetCurrentInContext(activity: Activity) {
        val reactContextClass = ReactContext::class.java
        val mCurrentActivityField = reactContextClass.getDeclaredField("mCurrentActivity")
        mCurrentActivityField.isAccessible = true
        mCurrentActivityField.set(reactInstanceManager.currentReactContext, WeakReference(activity))
    }

}