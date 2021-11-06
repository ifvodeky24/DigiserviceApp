package com.example.core_util

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun Fragment.dismissKeyboard() =
context?.inputMethodManager?.hideSoftInputFromWindow(view?.rootView?.windowToken, 0)

private inline val Context.inputMethodManager
    get() = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager