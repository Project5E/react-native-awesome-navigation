package com.project5e.react.navigation.navigator

import android.os.Bundle
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.NavigatorProvider
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

const val KEY_PUSH_ID_STACK = "key_push_id_stack"
const val KEY_PRESENT_ID_STACK = "key_present_id_stack"

@Navigator.Name("react_fragment")
class RnFragmentNavigator(
    private val provider: NavigatorProvider,
) : Navigator<FragmentNavigator.Destination>() {

    enum class NavigationType { PUSH, PRESENT }
    enum class PopBackType { POP, DISMISS }

    var navigationType: NavigationType? = null
    var popBackType: PopBackType? = null

    // destination id
    val pushIdStack = ArrayDeque<Int>()
    val presentIdStack = ArrayDeque<Int>()

    private val pushNavigator by lazy { provider.getNavigator(RnPushFragmentNavigator::class.java) }
    private val presentNavigator by lazy { provider.getNavigator(RnPresentFragmentNavigator::class.java) }

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

    override fun onSaveState(): Bundle {
        return Bundle().apply {
            putIntArray(KEY_PUSH_ID_STACK, pushIdStack.toIntArray())
            putIntArray(KEY_PRESENT_ID_STACK, presentIdStack.toIntArray())
        }
    }

    override fun onRestoreState(savedState: Bundle) {
        super.onRestoreState(savedState)
        savedState.getIntArray(KEY_PUSH_ID_STACK)?.apply {
            pushIdStack.clear()
            forEach { pushIdStack.add(it) }
        }
        savedState.getIntArray(KEY_PRESENT_ID_STACK)?.apply {
            presentIdStack.clear()
            forEach { presentIdStack.add(it) }
        }
    }

    private fun push(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) = pushNavigator.navigate(destination, args, navOptions, navigatorExtras)
        ?.apply { pushIdStack.add(destination.id) }

    private fun present(
        destination: FragmentNavigator.Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Extras?
    ) = presentNavigator.navigate(destination, args, navOptions, navigatorExtras)
        ?.apply { presentIdStack.add(destination.id) }

    private fun pop() = pushNavigator.popBackStack().apply { if (this) pushIdStack.removeLast() }

    private fun dismiss() = presentNavigator.popBackStack().apply {
        if (this) {
            // clear push stack
            pushIdStack.removeAll { it > presentIdStack.last }
            // remove self
            presentIdStack.removeLast()
        }
    }

    private fun recycle() {
        navigationType = null
        popBackType = null
    }

}
