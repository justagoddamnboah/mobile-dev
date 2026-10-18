package com.example.mobiledev.extensions

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment(layoutResId: Int) : Fragment(layoutResId) {
    private val tag = this::class.java.simpleName

    override fun onAttach(context: Context) { super.onAttach(context); Log.d(tag, "onAttach()") }
    override fun onCreate(savedInstanceState: Bundle?) { super.onCreate(savedInstanceState); Log.d(tag, "onCreate()") }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) { super.onViewCreated(view, savedInstanceState); Log.d(tag, "onViewCreated()") }
    override fun onStart() { super.onStart(); Log.d(tag, "onStart()") }
    override fun onResume() { super.onResume(); Log.d(tag, "onResume()") }
    override fun onPause() { super.onPause(); Log.d(tag, "onPause()") }
    override fun onStop() { super.onStop(); Log.d(tag, "onStop()") }
    override fun onDestroyView() { super.onDestroyView(); Log.d(tag, "onDestroyView()") }
    override fun onDestroy() { super.onDestroy(); Log.d(tag, "onDestroy()") }
    override fun onDetach() { super.onDetach(); Log.d(tag, "onDetach()") }
}