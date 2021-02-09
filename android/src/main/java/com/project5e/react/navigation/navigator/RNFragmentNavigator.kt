package com.project5e.react.navigation.navigator

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.navigation.Navigator
import com.project5e.react.navigation.view.RNComponentLifecycle

@Navigator.Name("react_fragment")
class RNFragmentNavigator(
    private val context: Context,
    private val manager: FragmentManager,
    private val containerId: Int
) : HackNavigator(context, manager, containerId) {

    override fun createWithDisplay(ft: FragmentTransaction, className: String, args: Bundle?, tag: String?): Fragment {
        val frag = manager.findFragmentByTag(tag) ?: instantiateFragment(context, manager, className, args)
        frag.arguments = args
        if (frag.isAdded || mBackStack.isEmpty()) {
            ft.replace(containerId, frag, tag)
        } else {
            ft.add(containerId, frag, tag)
        }
        return frag
    }

    override fun pushLifecycleEffect(nextFragment: Fragment) {
        val destId = mBackStack.peekLast()
        val currentFragment = destId?.let { manager.findFragmentByTag(it.toString()) } ?: return

        if (currentFragment is RNComponentLifecycle) {
            nextFragment.lifecycle.addObserver(object : LifecycleObserver {
                @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
                fun onCreate() {
                    currentFragment.viewDidDisappear()
                    nextFragment.lifecycle.removeObserver(this)
                }
            })
        }
    }

    override fun popLifecycleEffect() {
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
                    if (!prevFragment.isDetached) {
                        prevFragment.viewDidAppear()
                    }
                    currentFragment.lifecycle.removeObserver(this)
                }
            })
        }
    }

}
