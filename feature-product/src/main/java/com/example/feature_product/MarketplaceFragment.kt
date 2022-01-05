package com.example.feature_product

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductGetAll
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_home.store.ProductViewModel
import com.example.feature_home.viewHolder.ItemProductViewHolder
import com.example.feature_product.databinding.FragmentMarketplaceBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class MarketplaceFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentMarketplaceBinding? = null
    private val binding get() = _binding!!
    private lateinit var preferenceManager: PreferenceManager
    private val productViewModel: ProductViewModel by viewModel()

    private val jualId by lazy { (activity as ProductActivity).jualId }
    private val status by lazy { (activity as ProductActivity).status }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMarketplaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (status == "2") {
            findNavController().navigate(R.id.detailProductFragment)
        } else {
            preferenceManager = PreferenceManager(requireActivity())

            productViewModel.productGetAll()
            observeProductGetAll()
        }
    }

    private fun observeProductGetAll() {
        productViewModel.productGetAllResponse.observe(viewLifecycleOwner) { productAll ->
            when (productAll) {
                is ApiEvent.OnProgress -> {
                    binding.shimmerMarketplace.showShimmer(true)
                }
                is ApiEvent.OnSuccess -> productAll.getData().let {
                    onDataProductAllLoaded(productAll.getData()!!)
                    binding.shimmerMarketplace.hideShimmer()
                }
                is ApiEvent.OnFailed -> {
                    binding.shimmerMarketplace.hideShimmer()
                }
            }
        }
    }

    private fun onDataProductAllLoaded(data: List<ProductGetAll>) {
        val userId = preferenceManager.getString(Constants.ID)
        val filter = data.filter { it.jualUserId.toString() != userId }
        if (filter.isNotEmpty()) {
            binding.rvMarketplace.setup {
                withDataSource(dataSourceTypedOf(filter))
                withItem<ProductGetAll, ItemProductViewHolder>(R.layout.item_produk_terbaru) {
                    onBind(::ItemProductViewHolder) { _, item ->
                        tvProductName.text = item.jualJudul
                        tvProductDesciption.text = item.jualDeskripsi
                        Glide.with(requireActivity())
                            .load(APP_PRODUCT_IMAGES_URL + item.pathPhoto)
                            .centerCrop()
                            .into(ivProductPhoto)
                    }

                    onClick {
                        val mBundle = Bundle()
                        mBundle.putString(JUAL_ID, item.jualId.toString())
                        findNavController().navigate(R.id.detailProductFragment, mBundle)
                    }
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        val JUAL_ID = "jualId"
    }

}