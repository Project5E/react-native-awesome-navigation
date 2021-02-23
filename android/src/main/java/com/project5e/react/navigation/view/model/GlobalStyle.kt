package com.project5e.react.navigation.view.model

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.navigation.AnimBuilder
import com.project5e.react.navigation.NavigationManager
import com.project5e.react.navigation.R

data class GlobalStyle(
    @ColorInt
    val componentContainerBackgroundColor: Int = Color.WHITE,

    val pushAnim: AnimBuilder.() -> Unit = anim_right_enter_right_exit,

    val presentAnim: AnimBuilder.() -> Unit = anim_bottom_enter_bottom_exit
) {

    @Suppress("NEWER_VERSION_IN_SINCE_KOTLIN")
    @SinceKotlin("999.9") // Hide from Kotlin code, this method is only for Java code
    fun newBuilder() = Builder(this)

    class Builder internal constructor(style: GlobalStyle) {
        private var componentContainerBackgroundColor = style.componentContainerBackgroundColor
        private var pushAnim = style.pushAnim
        private var presentAnim = style.presentAnim

        fun build() = NavigationManager.style.copy(
            componentContainerBackgroundColor = componentContainerBackgroundColor,
            pushAnim = pushAnim,
            presentAnim = presentAnim
        )

        fun setComponentContainerBackgroundColor(@ColorInt componentContainerBackgroundColor: Int) =
            apply { this.componentContainerBackgroundColor = componentContainerBackgroundColor }

        fun setPushAnim(pushAnim: AnimBuilder.() -> Unit) =
            apply { this.pushAnim = pushAnim }

        fun setPresentAnim(presentAnim: AnimBuilder.() -> Unit) =
            apply { this.presentAnim = presentAnim }

    }
}

val anim_right_enter_right_exit: AnimBuilder.() -> Unit = {
    enter = R.anim.navigation_slide_in_right
    exit = R.anim.navigation_fade_out
    popExit = R.anim.navigation_slide_out_right
}

val anim_top_enter_top_exit: AnimBuilder.() -> Unit = {
    enter = R.anim.navigation_top_enter
    popExit = R.anim.navigation_top_exit
//    exit = android.R.anim.fade_out
//    popEnter = android.R.anim.fade_in
}

val anim_bottom_enter_bottom_exit: AnimBuilder.() -> Unit = {
    enter = R.anim.navigation_bottom_enter
    popExit = R.anim.navigation_bottom_exit
//    exit = android.R.anim.fade_out
//    popEnter = android.R.anim.fade_in
}
