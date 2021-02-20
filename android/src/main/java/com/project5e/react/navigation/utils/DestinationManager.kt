package com.project5e.react.navigation.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import com.facebook.react.bridge.Arguments
import com.project5e.react.navigation.NavigationManager
import com.project5e.react.navigation.model.ARG_COMPONENT_NAME
import com.project5e.react.navigation.model.ARG_LAUNCH_OPTIONS
import com.project5e.react.navigation.model.ARG_OPTIONS_SCREEN_ID
import com.project5e.react.navigation.model.Page
import com.project5e.react.navigation.navigator.RNFragmentNavigator
import com.project5e.react.navigation.view.RNActivity
import com.project5e.react.navigation.view.RNFragment

class DestinationManager(private val navController: NavController) {

    val navigator: RNFragmentNavigator = navController.navigatorProvider[RNFragmentNavigator::class]

    val lastPushDestination: FragmentNavigator.Destination?
        get() = if (navigator.pushDestinationStack.isEmpty()) null else navigator.pushDestinationStack.peekLast()

    val lastPresentDestination: FragmentNavigator.Destination?
        get() = if (navigator.presentDestinationStack.isEmpty()) null else navigator.presentDestinationStack.peekLast()

    var navigationType: RNFragmentNavigator.NavigationType? = null
        set(value) {
            navigator.navigationType = value
            field = value
        }
    var popBackType: RNFragmentNavigator.PopBackType? = null
        set(value) {
            navigator.popBackType = value
            field = value
        }

    fun buildDestination(
        destinationName: String,
        options: Bundle? = null,
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

    fun buildDestination(page: Page): NavDestination = buildDestination(page.rootName, Arguments.toBundle(page.options))

    private fun addDestinationAndNavigate(
        page: Page,
        args: Bundle?,
        navOptions: NavOptions?
    ) {
        val destination = buildDestination(page)
        navController.graph.addDestination(destination)
        navController.navigate(destination.id, args, navOptions)
    }

    fun addDestinationAndPush(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(NavigationManager.style.pushAnim) }
    ) {
        navigator.navigationType = RNFragmentNavigator.NavigationType.PUSH
        addDestinationAndNavigate(page, args, navOptions)
    }

    fun addDestinationAndPresent(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(NavigationManager.style.presentAnim) }
    ) {
        navigator.navigationType = RNFragmentNavigator.NavigationType.PRESENT
        addDestinationAndNavigate(page, args, navOptions)
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

}

