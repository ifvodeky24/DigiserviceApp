package com.example.feature_product

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductDetail
import com.example.core_navigation.ModuleNavigator
import com.example.core_resource.showApiFailedDialog
import com.example.core_util.hideProgress
import com.example.core_util.showProgress
import com.example.feature_home.store.ProductViewModel
import com.example.feature_product.databinding.FragmentDetailProductBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DetailProductFragment : Fragment(), ModuleNavigator, View.OnClickListener {
    private val jualId by lazy { (activity as ProductActivity).jualId }

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModel()

    private var beliPembeli: Int? = null

    private var byKurir: String = ""

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

        setupDisplay()

        setupInput()

        observeProductDetail()

        observeBuyProduct()
    }

    private fun setupDisplay() {
        productViewModel.productDetail(jualId.toInt())
    }

    private fun setupInput() {
        with(binding){
            backImageView.setOnClickListener { activity?.finish() }
            btnOrder.setOnClickListener(this@DetailProductFragment)
        }

    }

    private fun observeBuyProduct() {
        productViewModel.buyProductResponse.observe(viewLifecycleOwner){ event ->
            when(event){
                is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> {
                    hideProgress(true)
                    navigateToHomeActivity(false)
                }
                is ApiEvent.OnFailed -> {
                    hideProgress(true)
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }

    private fun showProgress() = with(binding) {
        listOf(
            btnByKurir, btnOrder
        ).forEach { it.isEnabled = false }

        btnOrder.showProgress()
    }

    private fun hideProgress(isEnable: Boolean = false) = with(binding) {
        btnOrder.postDelayed(
            {
                listOf(
                    btnByKurir, btnOrder
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnOrder.hideProgress("Beli") {
            isEnable && jualId.isNotBlank() && "$beliPembeli".isNotBlank() && byKurir.isNotBlank()
        }
    }


    private fun observeProductDetail() {
        productViewModel.productDetailResponse.observe(viewLifecycleOwner, { productDetail ->
            when(productDetail){
                is ApiEvent.OnProgress -> {
                }
                is ApiEvent.OnSuccess -> productDetail.getData().let {
                    Timber.d(" productDetail dfdf ${productDetail.getData()}")
                    onDataProductDetailLoaded(productDetail.getData()!!)
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" productDetail booom ${productDetail.getException()}")
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun onDataProductDetailLoaded(data: ProductDetail) {

        beliPembeli = data.jualUserId

        with(binding){
            tvProductStatus.text = data.jualStatus
            tvName.text = data.jualJudul
            tvDate.text = data.jualTglPenjualan
            tvPrice.text = "Rp. ${data.jualHarga}"
            tvTypeName.text = data.jenisNama
            tvDescription.text = data.jualDeskripsi
            Glide.with(requireActivity())
                .load(APP_PRODUCT_IMAGES_URL+data.fotoProduk)
                .fitCenter()
                .into(ivProduct)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.btn_order -> {
                buyProduct()
            }
        }
    }

    private fun buyProduct() {
        byKurir = if (binding.kurirYes.isChecked) "Ya" else "Tidak"
        productViewModel.buyProduct(jualId.toInt(), byKurir, beliPembeli ?: 0)
    }
}