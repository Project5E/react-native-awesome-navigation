package com.project5e.react.navigation.navigator

import android.os.Bundle
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator

@Navigator.Name("react_fragment")
class RNFragmentNavigator(
    private val provider: NavigatorProvider,
) : Navigator<FragmentNavigator.Destination>() {

    enum class NavigationType { PUSH, PRESENT }
    enum class PopBackType { POP, DISMISS }

    var navigationType: NavigationType? = null
    var popBackType: PopBackType? = null

    private val pushNavigator by lazy { provider.getNavigator(RNPushFragmentNavigator::class.java) }
    private val presentNavigator by lazy { provider.getNavigator(RNPresentFragmentNavigator::class.java) }

    override fun createDestination() = FragmentNavigator.Destination(this)

    override fun navigate(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        val navigator = getNavigator(navigationType ?: NavigationType.PUSH)
        return navigator.navigate(destination, args, navOptions, navigatorExtras)
    }

    override fun popBackStack(): Boolean {
        val navigator = getNavigator(popBackType ?: PopBackType.POP)
        recycle()
        return navigator.popBackStack()
    }

    private fun getNavigator(navigationType: NavigationType): Navigator<FragmentNavigator.Destination> {
        return when (navigationType) {
            NavigationType.PUSH -> pushNavigator
            NavigationType.PRESENT -> presentNavigator
        }
    }

    private fun getNavigator(popBackType: PopBackType): Navigator<FragmentNavigator.Destination> {
        return when (popBackType) {
            PopBackType.POP -> pushNavigator
            PopBackType.DISMISS -> presentNavigator
        }
    }

    private fun recycle() {
        navigationType = null
        popBackType = null
    }

}
