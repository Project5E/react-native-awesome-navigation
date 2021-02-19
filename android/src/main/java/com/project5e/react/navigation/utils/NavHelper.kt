package com.project5e.react.navigation.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.navigation.*
import androidx.navigation.fragment.NavHostFragment
import com.project5e.react.navigation.model.ARG_COMPONENT_NAME
import com.project5e.react.navigation.model.ARG_LAUNCH_OPTIONS
import com.project5e.react.navigation.model.ARG_OPTIONS_SCREEN_ID
import com.project5e.react.navigation.navigator.RNFragmentNavigator
import com.project5e.react.navigation.navigator.RNPresentFragmentNavigator
import com.project5e.react.navigation.navigator.RNPushFragmentNavigator
import com.project5e.react.navigation.view.RNActivity
import com.project5e.react.navigation.view.RNFragment

fun createRNPushFragmentNavigator(navHostFragment: NavHostFragment) =
    RNPushFragmentNavigator(
        navHostFragment.requireContext(),
        navHostFragment.childFragmentManager,
        navHostFragment.id
    )

fun createRNPresentFragmentNavigator(navHostFragment: NavHostFragment) =
    RNPresentFragmentNavigator(
        navHostFragment.requireContext(),
        navHostFragment.childFragmentManager,
        navHostFragment.id
    )

fun NavController.setGraph(startDestination: NavDestination?) {
    startDestination ?: return

    val navigator = navigatorProvider.getNavigator(NavGraphNavigator::class.java)
    graph = navigator.createDestination().also {
        it.addDestination(startDestination)
        it.startDestination = startDestination.id
    }
}

fun buildDestination(
    navigator: RNFragmentNavigator,
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

