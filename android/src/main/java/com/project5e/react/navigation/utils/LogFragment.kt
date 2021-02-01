package com.project5e.react.navigation.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.ReactFragment
import com.project5e.react.navigation.model.ARG_COMPONENT_NAME

abstract class LogFragment : ReactFragment() {

    companion object {
        var TAG: String? = null
    }

    private var mainComponentName: String = ""

    override fun setArguments(args: Bundle?) {
        args?.getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
        super.setArguments(args)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        TAG?.let { Log.e(it, "onAttach $mainComponentName") }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        TAG?.let { Log.e(it, "onCreate $mainComponentName") }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        TAG?.let { Log.e(it, "onCreateView $mainComponentName") }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TAG?.let { Log.e(it, "onViewCreated $mainComponentName") }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        TAG?.let { Log.e(it, "onActivityCreated $mainComponentName") }
    }

    override fun onStart() {
        super.onStart()
        TAG?.let { Log.e(it, "onStart $mainComponentName") }
    }

    override fun onResume() {
        super.onResume()
        TAG?.let { Log.e(it, "onResume $mainComponentName") }
    }

    override fun onPause() {
        super.onPause()
        TAG?.let { Log.e(it, "onPause $mainComponentName") }
    }

    override fun onStop() {
        super.onStop()
        TAG?.let { Log.e(it, "onStop $mainComponentName") }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        TAG?.let { Log.e(it, "onDestroyView $mainComponentName") }
    }

    override fun onDestroy() {
        super.onDestroy()
        TAG?.let { Log.e(it, "onDestroy $mainComponentName") }
    }

    override fun onDetach() {
        super.onDetach()
        TAG?.let { Log.e(it, "onDetach $mainComponentName") }
    }

}