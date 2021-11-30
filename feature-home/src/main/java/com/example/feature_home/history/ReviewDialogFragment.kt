package com.example.feature_home.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import com.example.core_util.toEditable
import com.example.feature_home.R
import com.example.feature_home.databinding.ReviewDialogBinding
import com.example.feature_home.history.ReviewDialogFragment.Companion.TAG
import com.example.feature_home.store.ProductViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReviewDialogFragment(
    val beliId: Int,
    val isEdit: Boolean,
    val value: Float,
    val desc: String,
) : BottomSheetDialogFragment() {

    private var _binding: ReviewDialogBinding? = null
    private val binding get() = _binding!!

    override fun getTheme() = R.style.BaseTheme_BottomSheet

    private val historyViewModel: HistoryViewModel by sharedViewModel()
    private val productViewModel: ProductViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = ReviewDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setView()
    }

    private fun setView() {
        
        with(binding){
            if (isEdit){
                rating.isClickable = true
                rating.rating = value
                edtDescReview.text = desc.toEditable()
                btnDialogReview.visibility = View.GONE
            }
            rating.setOnRatingBarChangeListener { ratingBar, fl, b ->
                val ratingValue = ratingBar.rating
                historyViewModel.rating = ratingValue
            }
            btnDialogReview.setOnClickListener {
                historyViewModel.beliId = beliId
                historyViewModel.ratingDesc = "${edtDescReview.text}"
                productViewModel.reviewProduct(historyViewModel.beliId, historyViewModel.rating, historyViewModel.ratingDesc)
                setFragmentResult(KEY_RESULT_SUBMIT, bundleOf(KEY_BUNDLE_SUBMIT to TRUE))
                this@ReviewDialogFragment.dismiss()
            }
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
        const val TAG = "tag_review_dialog"
    }
}

fun Fragment.showDialogReview(beliId:Int, isEdit: Boolean = false, rating: Float = 0f, desc: String = ""){
    ReviewDialogFragment(beliId, isEdit, rating, desc).show(childFragmentManager, TAG)
}