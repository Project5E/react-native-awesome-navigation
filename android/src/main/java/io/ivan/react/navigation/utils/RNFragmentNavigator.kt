package io.ivan.react.navigation.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.annotation.IdRes
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.NavDestination
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.FragmentNavigator
import io.ivan.react.navigation.model.ARG_NAV_PENETRABLE
import io.ivan.react.navigation.view.RNComponentLifecycle
import java.util.*

private const val TAG = "RNFragmentNavigator"

@Navigator.Name("react_fragment")
class RNFragmentNavigator(
    private val context: Context,
    private val manager: FragmentManager,
    private val containerId: Int
) : FragmentNavigator(context, manager, containerId) {

    private val mBackStack by lazy { getBackStack() }

    override fun navigate(
        destination: Destination, args: Bundle?,
        navOptions: NavOptions?, navigatorExtras: Navigator.Extras?
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
        val tag = destId.toString()
        val frag = manager.findFragmentByTag(tag)
            ?: manager.fragmentFactory.instantiate(context.classLoader, className)
        frag.arguments = args

        val isPenetrate = args?.getBoolean(ARG_NAV_PENETRABLE)
        if (isPenetrate != null && !isPenetrate) {
            pushLifecycleEffect()
        }

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

        val initialNavigation = mBackStack.isEmpty()
        val isSingleTopReplacement = (navOptions != null && !initialNavigation
                && navOptions.shouldLaunchSingleTop()
                && mBackStack.peekLast() == destId)

        if (frag.isAdded || initialNavigation) {
            ft.replace(containerId, frag, tag)
        } else {
            ft.add(containerId, frag, tag)
        }

        ft.setPrimaryNavigationFragment(frag)

        val isAdded = when {
            initialNavigation -> {
                true
            }
            isSingleTopReplacement -> {
                if (mBackStack.size > 1) {
                    manager.popBackStack(
                        generateBackStackName(mBackStack.size, mBackStack.peekLast()),
                        FragmentManager.POP_BACK_STACK_INCLUSIVE
                    )
                    ft.addToBackStack(generateBackStackName(mBackStack.size, destId))
                }
                false
            }
            else -> {
                ft.addToBackStack(generateBackStackName(mBackStack.size + 1, destId))
                true
            }
        }
        if (navigatorExtras is Extras) {
            for ((key, value) in navigatorExtras.sharedElements) {
                ft.addSharedElement(key, value)
            }
        }
        ft.setReorderingAllowed(true)
        ft.commit()

        return if (isAdded) {
            mBackStack.add(destId)
            destination
        } else {
            null
        }
    }

    override fun popBackStack(): Boolean {
        popLifecycleEffect()
        return super.popBackStack()
    }

    private fun generateBackStackName(backStackIndex: Int, destId: Int): String {
        return "$backStackIndex-$destId"
    }

    private fun getBackStack(): ArrayDeque<Int> {
        val fragmentNavigatorClass = FragmentNavigator::class.java
        val mBackStackField = fragmentNavigatorClass.getDeclaredField("mBackStack")
        mBackStackField.isAccessible = true
        return mBackStackField.get(this) as ArrayDeque<Int>
    }

    private fun pushLifecycleEffect() {
        val destId = mBackStack.peekLast()
        val currentFragment = destId?.let { manager.findFragmentByTag(it.toString()) } ?: return

        if (currentFragment is RNComponentLifecycle) {
            currentFragment.viewDidDisappear()
        }
    }

    private fun popLifecycleEffect() {
        if (mBackStack.size < 2) return
        val backList = mBackStack.toArray()
        val last1 = backList[backList.lastIndex]
        val last2 = backList[backList.lastIndex - 1]
        val currentFragment = manager.findFragmentByTag(last1.toString()) ?: return
        val prevFragment = manager.findFragmentByTag(last2.toString()) ?: return

        if (prevFragment is RNComponentLifecycle) {
            currentFragment.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                fun onDestroy() {
                    prevFragment.viewDidAppear()
                    currentFragment.lifecycle.removeObserver(this)
                }
            })
        }
    }

}