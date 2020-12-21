package io.ivan.react.navigation

import android.app.Activity
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.bridge.ReactContext
import io.ivan.react.navigation.utils.RNNavigationException
import java.lang.ref.WeakReference

object NavigationManager {

    private var _reactNativeHost: ReactNativeHost? = null

    val reactNativeHost: ReactNativeHost
        @JvmStatic
        get() = requireReactNativeHost()

    val reactInstanceManager: ReactInstanceManager
        @JvmStatic
        get() = requireReactNativeHost().reactInstanceManager

    @JvmStatic
    fun install(reactNativeHost: ReactNativeHost) {
        this._reactNativeHost = reactNativeHost
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
    fun requireReactNativeHost(): ReactNativeHost =
        _reactNativeHost ?: throw RNNavigationException("must call NavigationManager#install first")


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