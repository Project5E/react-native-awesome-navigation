package com.project5e.react.navigation.view

import android.os.Bundle
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactDelegate

open class RNBaseActivity : ReactActivity() {

    private class InnerReactActivityDelegate(activity: ReactActivity?, mainComponentName: String?) :
        ReactActivityDelegate(activity, mainComponentName) {

        private lateinit var reactDelegate: ReactDelegate

        override fun onCreate(savedInstanceState: Bundle?) {
            reactDelegate = ReactDelegate(plainActivity, reactNativeHost, mainComponentName, launchOptions)
            setReactDelegate(reactDelegate)
        }

        private fun setReactDelegate(reactDelegate: ReactDelegate) {
            val reactActivityDelegateClass = ReactActivityDelegate::class.java
            val mReactDelegateField = reactActivityDelegateClass.getDeclaredField("mReactDelegate")
            mReactDelegateField.isAccessible = true
            mReactDelegateField.set(this, reactDelegate)
        }
    }

    override fun createReactActivityDelegate(): ReactActivityDelegate {
        return InnerReactActivityDelegate(this, mainComponentName)
    }

}
