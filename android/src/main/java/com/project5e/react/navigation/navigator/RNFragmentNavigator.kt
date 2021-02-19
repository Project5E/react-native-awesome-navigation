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
) : Navigator<FragmentNavigator.Destination>() {

    enum class NavigationType { PUSH, PRESENT }
    enum class PopBackType { POP, DISMISS }

    var navigationType: NavigationType? = null
    var popBackType: PopBackType? = null

    val lastPushDestination: FragmentNavigator.Destination?
        get() = if (pushDestinationStack.isEmpty()) null else pushDestinationStack.peekLast()

    val lastPresentDestination: FragmentNavigator.Destination?
        get() = if (presentDestinationStack.isEmpty()) null else presentDestinationStack.peekLast()

    private val pushNavigator by lazy { provider.getNavigator(RNPushFragmentNavigator::class.java) }
    private val presentNavigator by lazy { provider.getNavigator(RNPresentFragmentNavigator::class.java) }

    private val pushDestinationStack = ArrayDeque<FragmentNavigator.Destination>()
    private val presentDestinationStack = ArrayDeque<FragmentNavigator.Destination>()

    override fun createDestination() = FragmentNavigator.Destination(this)

    override fun navigate(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ): NavDestination? {
        return when (navigationType ?: NavigationType.PUSH) {
            NavigationType.PUSH -> push(destination, args, navOptions, navigatorExtras)
            NavigationType.PRESENT -> present(destination, args, navOptions, navigatorExtras)
        }
    }

    override fun popBackStack(): Boolean {
        return when (popBackType ?: PopBackType.POP) {
            PopBackType.POP -> pop()
            PopBackType.DISMISS -> dismiss()
        }.apply { recycle() }
    }

    private fun push(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) = pushNavigator.navigate(destination, args, navOptions, navigatorExtras)
        ?.apply { pushDestinationStack.add(destination) }

    private fun present(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) = presentNavigator.navigate(destination, args, navOptions, navigatorExtras)
        ?.apply { presentDestinationStack.add(destination) }

    private fun pop() = pushNavigator.popBackStack().apply { if (this) pushDestinationStack.removeLast() }

    private fun dismiss() = presentNavigator.popBackStack().apply {
        if (this) {
            // clear push stack
            lastPresentDestination?.let { last ->
                pushDestinationStack.removeAll {
                    it.id > last.id
                }
            }
            // remove self
            presentDestinationStack.removeLast()
        }
    }

    private fun recycle() {
        navigationType = null
        popBackType = null
    }

}
