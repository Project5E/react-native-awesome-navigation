package com.project5e.react.navigation.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import com.project5e.react.navigation.navigator.RnFragmentNavigator
import com.project5e.react.navigation.navigator.RnPresentFragmentNavigator
import com.project5e.react.navigation.navigator.RnPushFragmentNavigator

fun createRnFragmentNavigator(provider: NavigatorProvider) = RnFragmentNavigator(provider)

fun createRnPushFragmentNavigator(navHostFragment: NavHostFragment) =
    RnPushFragmentNavigator(
        navHostFragment.requireContext(),
        navHostFragment.childFragmentManager,
        navHostFragment.id
    )

fun createRnPresentFragmentNavigator(navHostFragment: NavHostFragment) =
    RnPresentFragmentNavigator(
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
