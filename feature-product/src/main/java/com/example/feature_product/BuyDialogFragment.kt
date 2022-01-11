package com.example.feature_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.example.feature_home.account.LogoutDialogFragment
import com.example.feature_product.databinding.FragmentBuyDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BuyDialogFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBuyDialogBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = com.example.feature_home.R.style.BaseTheme_BottomSheet

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding =
            FragmentBuyDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnCencel.setOnClickListener {
            this.dismiss()
        }

        binding.btnBeli.setOnClickListener {
            setFragmentResult(LogoutDialogFragment.KEY_RESULT_SUBMIT, bundleOf(LogoutDialogFragment.KEY_BUNDLE_SUBMIT to LogoutDialogFragment.TRUE))
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