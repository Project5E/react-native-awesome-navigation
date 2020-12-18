package io.ivan.react.navigation.utils

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

private const val TAG = "LifecycleFragment"

abstract class LifecycleFragment : Fragment() {

    abstract var name: String?

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Log.d(TAG, "onAttach $name")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate $name")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView $name")
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated $name")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated $name")
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart $name")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume $name")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause $name")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop $name")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(TAG, "onDestroyView $name")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy $name")
    }

    override fun onDetach() {
        super.onDetach()
        Log.d(TAG, "onDetach $name")
    }

}