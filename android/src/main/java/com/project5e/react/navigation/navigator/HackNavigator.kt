package com.project5e.react.navigation.navigator

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import java.util.*

private const val TAG = "HackNavigator"

open class HackNavigator(
    private val context: Context,
    private val manager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    open val mBackStack by lazy { hackBackStack() }

    override fun navigate(
        destination: Destination,
        args: Bundle?,
        navOptions: NavOptions?,
        navigatorExtras: Navigator.Extras?
    ): NavDestination? {
        if (manager.isStateSaved) {
            Log.i(TAG, "Ignoring navigate() call: FragmentManager has already saved its state")
            return null
        }
        var className = destination.className
        if (className[0] == '.') {
            className = context.packageName + className
        }
        @IdRes val destId = destination.id

        val ft = manager.beginTransaction()
        var enterAnim = navOptions?.enterAnim ?: -1
        var exitAnim = navOptions?.exitAnim ?: -1
        var popEnterAnim = navOptions?.popEnterAnim ?: -1
        var popExitAnim = navOptions?.popExitAnim ?: -1
        if (enterAnim != -1 || exitAnim != -1 || popEnterAnim != -1 || popExitAnim != -1) {
            enterAnim = if (enterAnim != -1) enterAnim else 0
            exitAnim = if (exitAnim != -1) exitAnim else 0
            popEnterAnim = if (popEnterAnim != -1) popEnterAnim else 0
            popExitAnim = if (popExitAnim != -1) popExitAnim else 0
            ft.setCustomAnimations(enterAnim, exitAnim, popEnterAnim, popExitAnim)
        }

        val frag = createWithDisplay(ft, className, args, destId.toString())
        ft.setPrimaryNavigationFragment(frag)
        navigateLifecycleEffect(frag)

        val initialNavigation = mBackStack.isEmpty()
        val isSingleTopReplacement = (navOptions != null
                && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)

        when {
            isSingleTopReplacement -> {
                if (mBackStack.size > 1) {
                    manager.popBackStack(
                        generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
                }
            }
            else -> {
                ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
            }
        }

        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key!!, value!!)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()

        return if (!isSingleTopReplacement) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    override fun popBackStack(): Boolean {
        popLifecycleEffect()
        if (mBackStack.isEmpty()) {
            return false
        }
        if (manager.isStateSaved) {
            Log.i(TAG, "Ignoring popBackStack() call: FragmentManager has already saved its state")
            return false
        }
        manager.popBackStack(
            generateBackStackName(mBackStack.size, mBackStack.peekLast()),
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
        mBackStack.removeLast()
        return true
    }

    open fun createWithDisplay(
        ft: FragmentTransaction,
        className: String,
        args: Bundle?,
        tag: String?
    ): Fragment {
        val frag = instantiateFragment(context, manager, className, args)
        frag.arguments = args
        ft.replace(containerId, frag, tag)
        return frag
    }

    open fun navigateLifecycleEffect(nextFragment: Fragment) {}

    open fun popLifecycleEffect() {}

    private fun hackBackStack(): ArrayDeque<Int> {
        val fragmentNavigatorClass = FragmentNavigator::class.java
        val mBackStackField = fragmentNavigatorClass.getDeclaredField("mBackStack")
        mBackStackField.isAccessible = true
        return mBackStackField.get(this) as ArrayDeque<Int>
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }

}
