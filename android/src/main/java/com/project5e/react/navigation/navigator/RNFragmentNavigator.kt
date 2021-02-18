package com.project5e.react.navigation.navigator

import android.os.Bundle
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

@Navigator.Name("react_fragment")
class RNFragmentNavigator(
    private val provider: NavigatorProvider,
) : Navigator<RNFragmentNavigator.Destination>() {

    private val navigatorTypes = ArrayDeque<Destination.NavigationType>()
    private val pushNavigator by lazy { provider.getNavigator(RNPushFragmentNavigator::class.java) }
    private val presentNavigator by lazy { provider.getNavigator(RNPresentFragmentNavigator::class.java) }

    override fun createDestination() = Destination(this)

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        navigatorTypes.add(destination.navigatorType)
        val navigator = getNavigator(destination.navigatorType)
        return navigator.navigate(destination, args, navOptions, navigatorExtras)
    }

    override fun popBackStack(): Boolean {
        if (navigatorTypes.isEmpty()) {
            return false
        }
        val navigator = getNavigator(navigatorTypes.peekLast())
        navigator.popBackStack()
        navigatorTypes.removeLast()
        return true
    }

    private fun getNavigator(navigationType: Destination.NavigationType): Navigator<FragmentNavigator.Destination> {
        return when (navigationType) {
            Destination.NavigationType.PUSH -> pushNavigator
            Destination.NavigationType.PRESENT -> presentNavigator
        }
    }

    class Destination(fragmentNavigator: Navigator<Destination>) : FragmentNavigator.Destination(fragmentNavigator) {

        enum class NavigationType { PUSH, PRESENT }

        var navigatorType: NavigationType = NavigationType.PUSH
    }

}
