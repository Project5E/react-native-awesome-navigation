package com.project5e.react.navigation.view

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.project5e.react.navigation.NavigationConstants.Companion.VIEW_DID_APPEAR
import com.project5e.react.navigation.NavigationConstants.Companion.VIEW_DID_DISAPPEAR
import com.project5e.react.navigation.NavigationEmitter.sendNavigationEvent

interface RNComponentLifecycle {
    fun viewDidAppear()
    fun viewDidDisappear()
}

fun sendViewAppearEvent(lifecycleOwner: LifecycleOwner, screenId: String, appear: Boolean) {
    // 当从前台进入后台时，不会触发 disappear, 这和 iOS 保持一致
    if (lifecycleOwner.lifecycle.currentState != Lifecycle.State.STARTED) {
        sendNavigationEvent(if (appear) VIEW_DID_APPEAR else VIEW_DID_DISAPPEAR, screenId)
    }
}
