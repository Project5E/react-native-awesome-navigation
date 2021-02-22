package com.project5e.react.navigation.navigator

import android.content.Context
import androidx.fragment.app.FragmentManager
import androidx.navigation.Navigator

@Navigator.Name("react_fragment_present")
class RNPresentFragmentNavigator(context: Context, manager: FragmentManager, containerId: Int) :
    RNBaseFragmentNavigator(context, manager, containerId)
