package com.project5e.react.navigation.utils

import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraphNavigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.NavHostFragment
import com.project5e.react.navigation.navigator.RNFragmentNavigator
import com.project5e.react.navigation.navigator.RNPresentFragmentNavigator
import com.project5e.react.navigation.navigator.RNPushFragmentNavigator

fun createRNFragmentNavigator(provider: NavigatorProvider) = RNFragmentNavigator(provider)

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
