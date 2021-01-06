package io.ivan.react.navigation.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import io.ivan.react.navigation.R
import io.ivan.react.navigation.model.ARG_COMPONENT_NAME
import io.ivan.react.navigation.model.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.model.ARG_OPTIONS_SCREEN_ID
import io.ivan.react.navigation.view.RNActivity
import io.ivan.react.navigation.view.RNFragment

fun createRNFragmentNavigator(navHostFragment: NavHostFragment) =
    RNFragmentNavigator(navHostFragment.requireContext(), navHostFragment.childFragmentManager, navHostFragment.id)

fun NavController.setGraph(startDestination: NavDestination?) {
    startDestination ?: return

    val navigator = navigatorProvider.getNavigator(NavGraphNavigator::class.java)
    graph = navigator.createDestination().also {
        it.addDestination(startDestination)
        it.startDestination = startDestination.id
    }
}

fun buildDestination(
    navigator: FragmentNavigator,
    destinationName: String,
    options: Bundle?
): NavDestination {
    return navigator.createDestination().apply {
        val viewId = ViewCompat.generateViewId()
        id = viewId
        className = RNFragment::class.java.name
        addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().let { arg ->
            arg.defaultValue = destinationName
            arg.build()
        })
        addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().let { arg ->
            arg.defaultValue = (options ?: Bundle()).also {
                it.putString(ARG_OPTIONS_SCREEN_ID, viewId.toString())
            }
            arg.build()
        })
    }
}

fun buildDestination(
    context: Context,
    activityNavigator: ActivityNavigator,
    destinationName: String,
    options: Bundle?
): NavDestination {
    return activityNavigator.createDestination().apply {
        val viewId = ViewCompat.generateViewId()
        id = viewId
        intent = Intent(context, RNActivity::class.java)
        addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().let { arg ->
            arg.defaultValue = destinationName
            arg.build()
        })
        addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().let { arg ->
            arg.defaultValue = (options ?: Bundle()).also {
                it.putString(ARG_OPTIONS_SCREEN_ID, viewId.toString())
            }
            arg.build()
        })
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
