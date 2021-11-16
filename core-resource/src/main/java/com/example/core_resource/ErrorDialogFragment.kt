package com.example.core_resource

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.airbnb.lottie.LottieAnimationView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class ErrorDialogFragment(private val onDismissListener: () -> Unit = {}) : BottomSheetDialogFragment() {

    private val assetRes by lazy { arguments?.getInt(KEY_ASSET_RESOURCE, 0) ?: 0 }
    private val isAnimation by lazy { arguments?.getBoolean(KEY_IS_ANIMATION, false) ?: false }
    private val textTitle by lazy { arguments?.getString(KEY_TITLE).orEmpty() }
    private val textContent by lazy { arguments?.getString(KEY_CONTENT).orEmpty() }

    override fun getTheme() = R.style.CoreTheme_BottomSheet_Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_error_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<LottieAnimationView>(R.id.av)?.let {
            if (isAnimation) {
                it.setAnimation(assetRes)
            } else {
                it.setImageResource(assetRes)
            }
        }
        view.findViewById<TextView>(R.id.tv_title)?.let {
            it.text = textTitle
        }
        view.findViewById<TextView>(R.id.tv_content)?.let {
            it.text = textContent
        }
        view.findViewById<ImageButton>(R.id.btn_close)?.let {
            it.setOnClickListener { dismiss() }
        }
        view.findViewById<MaterialButton>(R.id.btn_positive)?.let {
            it.setOnClickListener { dismiss() }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        onDismissListener()
        super.onDismiss(dialog)
    }

}

fun Fragment.showErrorDialog(
    assetRes: Int,
    isAnimation: Boolean,
    @StringRes titleRes: Int,
    @StringRes contentRes: Int,
    onDismissListener: () -> Unit
) {
    ErrorDialogFragment(onDismissListener).apply {
        arguments = bundleOf(
            KEY_ASSET_RESOURCE to assetRes,
            KEY_IS_ANIMATION to isAnimation,
            KEY_TITLE to this@showErrorDialog.getString(titleRes),
            KEY_CONTENT to this@showErrorDialog.getString(contentRes)
        )
    }.show(childFragmentManager, "error_dialog")
}

fun AppCompatActivity.showErrorDialog(
    assetRes: Int,
    isAnimation: Boolean,
    @StringRes titleRes: Int,
    @StringRes contentRes: Int,
    onDismissListener: () -> Unit
) {
    ErrorDialogFragment(onDismissListener).apply {
        arguments = bundleOf(
            KEY_ASSET_RESOURCE to assetRes,
            KEY_IS_ANIMATION to isAnimation,
            KEY_TITLE to this@showErrorDialog.getString(titleRes),
            KEY_CONTENT to this@showErrorDialog.getString(contentRes)
        )
    }.show(supportFragmentManager, "error_dialog")
}

private const val KEY_ASSET_RESOURCE = "key_asset_resource"
private const val KEY_IS_ANIMATION = "key_is_animation"
private const val KEY_TITLE = "key_title"
private const val KEY_CONTENT = "key_content"
