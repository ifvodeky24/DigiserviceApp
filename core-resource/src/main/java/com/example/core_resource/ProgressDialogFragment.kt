package com.example.core_resource

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class ProgressDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme() = R.style.CoreTheme_BottomSheet_Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_progress_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
    }

}

//region Fragment

fun Fragment.showProgressDialog() {
    ProgressDialogFragment().show(childFragmentManager, TAG)
}

fun Fragment.hideProgressDialog() {
    (childFragmentManager.findFragmentByTag(TAG) as? BottomSheetDialogFragment)?.dismiss()
}

//endregion
//region Activity


fun AppCompatActivity.showProgressDialog() {
    ProgressDialogFragment().show(supportFragmentManager, TAG)
}

fun AppCompatActivity.hideProgressDialog() {
    (supportFragmentManager.findFragmentByTag(TAG) as? BottomSheetDialogFragment)?.dismiss()
}

//endregion

private const val TAG = "progress_dialog"
