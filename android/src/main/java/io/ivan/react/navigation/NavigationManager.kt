package io.ivan.react.navigation

import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import io.ivan.react.navigation.utils.RNNavigationException


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
        _reactNativeHost ?: throw RNNavigationException("must call NavigationInitializer#install first")

}