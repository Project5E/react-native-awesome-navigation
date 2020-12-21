package io.ivan.react.navigation.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.ReactFragment

private const val TAG = "1van"

abstract class LifecycleFragment : ReactFragment() {

    private var mainComponentName: String = ""

    override fun setArguments(args: Bundle?) {
        args?.getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
        super.setArguments(args)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.e(TAG, "onAttach $mainComponentName")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate $mainComponentName")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.e(TAG, "onCreateView $mainComponentName")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e(TAG, "onViewCreated $mainComponentName")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.e(TAG, "onActivityCreated $mainComponentName")
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart $mainComponentName")
    }

    override fun onResume() {
        super.onResume()
        Log.e(TAG, "onResume $mainComponentName")
    }

    override fun onPause() {
        super.onPause()
        Log.e(TAG, "onPause $mainComponentName")
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop $mainComponentName")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.e(TAG, "onDestroyView $mainComponentName")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e(TAG, "onDestroy $mainComponentName")
    }

    override fun onDetach() {
        super.onDetach()
        Log.e(TAG, "onDetach $mainComponentName")
    }

}