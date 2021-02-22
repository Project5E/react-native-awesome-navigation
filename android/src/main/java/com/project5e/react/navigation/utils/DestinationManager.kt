package com.project5e.react.navigation.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.navigation.*
import androidx.navigation.fragment.FragmentNavigator
import com.facebook.react.bridge.Arguments
import com.project5e.react.navigation.NavigationManager
import com.project5e.react.navigation.NavigationManager.registeredDestination
import com.project5e.react.navigation.model.*
import com.project5e.react.navigation.navigator.RNFragmentNavigator
import com.project5e.react.navigation.navigator.RNFragmentNavigator.NavigationType.PRESENT
import com.project5e.react.navigation.navigator.RNFragmentNavigator.NavigationType.PUSH
import com.project5e.react.navigation.view.RNActivity
import com.project5e.react.navigation.view.RNFragment

class DestinationManager(private val context: Context, private val navController: NavController) {

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

    fun push(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(NavigationManager.style.pushAnim) }
    ) {
        navigateWithAdd(page, args, navOptions) {
            when (this) {
                is FragmentNavigator.Destination -> navigator.navigationType = PUSH
                is ActivityNavigator.Destination -> navigator.navigationType = null
            }
        }
    }

    fun present(
        page: Page,
        args: Bundle? = null,
        navOptions: NavOptions? = navOptions { anim(NavigationManager.style.presentAnim) }
    ) {
        navigateWithAdd(page, args, navOptions) {
            when (this) {
                is FragmentNavigator.Destination -> navigator.navigationType = PRESENT
                is ActivityNavigator.Destination -> navigator.navigationType = null
            }
        }
    }

    fun createDestination(page: Page) = createDestination(page.rootName, Arguments.toBundle(page.options))

    fun createDestination(name: String, args: Bundle?): NavDestination {
        val clazz = getViewControllerClass(name)
        return when {
            clazz.isAssignableFrom(RNFragment::class.java) -> {
                createRnFragmentDestination(name, args)
            }
            clazz.isAssignableFrom(RNActivity::class.java) -> {
                createRnActivityDestination(name, args)
            }
            clazz.isAssignableFrom(Fragment::class.java) -> {
                createFragmentDestination(clazz, name, args)
            }
            clazz.isAssignableFrom(Activity::class.java) -> {
                createActivityDestination(clazz, name, args)
            }
            else -> throw IllegalArgumentException("destination target class must be assignable from Activity or Fragment!")
        }
    }

    fun <T> createFragmentDestination(
        clazz: Class<T>,
        name: String,
        args: Bundle? = null
    ): FragmentNavigator.Destination {
        return navigator.createDestination().apply {
            id = ViewCompat.generateViewId()
            className = clazz.name
            addArgument(ARG_NAME, NavArgumentBuilder().apply { defaultValue = name }.build())
            args?.keySet()?.forEach { addArgument(it, NavArgumentBuilder().apply { defaultValue = args[it] }.build()) }
        }
    }

    fun <T> createActivityDestination(
        clazz: Class<T>,
        name: String,
        args: Bundle? = null
    ): ActivityNavigator.Destination {
        val navigator: ActivityNavigator = navController.navigatorProvider[ActivityNavigator::class]
        return navigator.createDestination().apply {
            id = ViewCompat.generateViewId()
            intent = Intent(context, clazz)
            addArgument(ARG_NAME, NavArgumentBuilder().apply { defaultValue = name }.build())
            args?.keySet()?.forEach { addArgument(it, NavArgumentBuilder().apply { defaultValue = args[it] }.build()) }
        }
    }

    fun createRnFragmentDestination(name: String, args: Bundle? = null): FragmentNavigator.Destination {
        return createFragmentDestination(RNFragment::class.java, name, args).apply {
            addRnArgument(name, args)
        }
    }

    fun createRnActivityDestination(name: String, args: Bundle? = null): ActivityNavigator.Destination {
        return createActivityDestination(RNActivity::class.java, name, args).apply {
            addRnArgument(name, args)
        }
    }

    private fun getViewControllerClass(name: String): Class<out Any?> {
        return registeredDestination[name] ?: throw IllegalArgumentException("Not registered!")
    }

    private fun NavDestination.addRnArgument(name: String, args: Bundle?) {
        addArgument(ARG_COMPONENT_NAME, NavArgumentBuilder().apply { defaultValue = name }.build())
        addArgument(ARG_LAUNCH_OPTIONS, NavArgumentBuilder().apply {
            defaultValue = (args ?: Bundle()).apply { putString(ARG_OPTIONS_SCREEN_ID, id.toString()) }
        }.build())
    }

    private fun navigateWithAdd(
        page: Page,
        args: Bundle?,
        navOptions: NavOptions?,
        applyDestination: (NavDestination.() -> Unit) = { }
    ) {
        val destination = createDestination(page).apply(applyDestination)
        navController.graph.addDestination(destination)
        navController.navigate(destination.id, args, navOptions)
    }

}

