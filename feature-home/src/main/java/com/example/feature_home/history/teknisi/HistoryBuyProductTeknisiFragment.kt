package com.example.feature_home.history.teknisi

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductBuyHistoryGetAll
import com.example.core_data.domain.store.ProductGetAll
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentHistoryBuyProductTeknisiBinding
import com.example.feature_home.store.ProductViewModel
import com.example.feature_home.viewHolder.ItemHistoryBuyProduct
import com.example.feature_home.viewHolder.ItemProductViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HistoryBuyProductTeknisiFragment : Fragment() {

    private var _binding: FragmentHistoryBuyProductTeknisiBinding? = null
    private val binding: FragmentHistoryBuyProductTeknisiBinding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModel()
    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryBuyProductTeknisiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUser()

        observeProductByUserId()
    }

    private fun observeUser() {
        accountViewModel.authUser.observe(viewLifecycleOwner) { auth ->
            if (auth != null) {
                productViewModel.historyBuyProductGetById(auth.id)
            }
        }
    }

    private fun observeProductByUserId() {
        productViewModel.historyBuyProduct.observe(viewLifecycleOwner) { productAll ->
            when (productAll) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> productAll.getData().let {
                    onDataProductAllLoaded(productAll.getData()!!)
                    Timber.d(" uiuiuiui ${productAll.getData()}")
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" booom ${productAll.getException()}")
                }
            }
        }
    }

    private fun onDataProductAllLoaded(data: List<ProductBuyHistoryGetAll>) {
        if (data.isNotEmpty()) {
            binding.rvHistoryProductTeknisi.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<ProductBuyHistoryGetAll, ItemHistoryBuyProduct>(R.layout.item_history_buy_produk) {
                    onBind(::ItemHistoryBuyProduct) { _, item ->

                        val sellerName = item.teknisiNama.run {
                            if (isEmpty()) {
                                item.pelangganNama
                            } else {
                                this
                            }
                        }

                        val productBuyDate = item.beliTglBooking.run {
                            if (item.beliStatus == "booking") {
                                this
                            } else {
                                item.beliTglBeli
                            }
                        }

                        tvProductName.text = item.jualJudul
                        tvSellerName.text = sellerName
                        tvProductPrice.text = item.jualHarga.toString()
                        tvProductBuyDate.text = productBuyDate
                        tvProductBuyStatus.text = item.beliStatus

                        Glide.with(requireActivity())
                            .load(APP_PRODUCT_IMAGES_URL+item.fotoProduk)
                            .into(ivProductPhoto)

                        btnGiveReview.setOnClickListener {

                        }
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}