package com.example.feature_home.account

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentLogoutDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class LogoutDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentLogoutDialogBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.BaseTheme_BottomSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding =
            FragmentLogoutDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCancel.setOnClickListener {
            this.dismiss()
        }

        binding.btnSubmit.setOnClickListener {
            setFragmentResult(KEY_RESULT_SUBMIT, bundleOf(KEY_BUNDLE_SUBMIT to TRUE))
            this.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val KEY_RESULT_SUBMIT = "keyResultSubmit"
        const val KEY_BUNDLE_SUBMIT = "keyBundleSubmit"
        const val TRUE = "true"
    }
}