package com.example.feature_product

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.core_data.api.ApiEvent
import com.example.feature_home.store.ProductViewModel
import com.example.feature_product.databinding.FragmentDetailProductBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DetailProductFragment : Fragment() {
    private val jualId by lazy { (activity as ProductActivity).jualId }

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        productViewModel.productDetail(jualId.toInt())

        observeProductDetail()
    }

    private fun observeProductDetail() {
        productViewModel.productDetailResponse.observe(viewLifecycleOwner, { productDetail ->
            when(productDetail){
                is ApiEvent.OnProgress -> {
                }
                is ApiEvent.OnSuccess -> productDetail.getData().let {
                    Timber.d(" productDetail dfdf ${productDetail.getData()}")
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" productDetail booom ${productDetail.getException()}")
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}