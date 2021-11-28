package com.example.feature_home.history.pelanggan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductBuyHistoryGetAll
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentHistoryServicePelangganBinding
import com.example.feature_home.history.teknisi.HistoryBuyProductTeknisiFragment
import com.example.feature_home.store.ProductViewModel
import com.example.feature_home.viewHolder.ItemHistoryBuyProduct
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HistoryProductPelangganFragment : Fragment() {

    private var _binding: FragmentHistoryServicePelangganBinding? = null
    private val binding: FragmentHistoryServicePelangganBinding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModel()
    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryServicePelangganBinding.inflate(inflater, container, false)
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
            binding.rvHistoryServicePelanggan.setup {
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
                            .load(item.fotoProduk)
                            .into(ivProductPhoto)

                        if (item.beliStatus != "booking") {
                            buttonActionContainer.visibility = View.GONE
                        }

                        btnGiveReview.setOnClickListener {

                        }

                        btnProductCancel.setOnClickListener {
                            showAlertDialog("Apakah kamu yang ingin membatalkan traksaksi ini?", "Batal", "Ya") {
                                updateStatusBeliProduk(item.beliId, HistoryBuyProductTeknisiFragment.Companion.StatusBeliProduk.Cancel())
                            }

                        }

                        btnProductFinish.setOnClickListener {
                            showAlertDialog("Apakah kamu yang ingin menyelesaikan traksaksi ini?", "Batal", "Ya") {
                                updateStatusBeliProduk(item.beliId, HistoryBuyProductTeknisiFragment.Companion.StatusBeliProduk.Finish())
                            }
                        }
                    }
                }
            }
        }
    }

    private fun updateStatusBeliProduk(beliId: Int, status: HistoryBuyProductTeknisiFragment.Companion.StatusBeliProduk) {
        productViewModel.updateStatusBeliProduct(beliId, status.status)

        productViewModel.updateStatusBeliProduct.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    observeUser()
                    val snackbarMessage = if (status.status == HistoryBuyProductTeknisiFragment.Companion.StatusBeliProduk.Cancel().status) {
                        "Pemesanan berhasil dibatalkan!"
                    } else {
                        "Produk telah diterima, terima kasih telah berbelanja disini!"
                    }
                    Snackbar.make(requireContext(), requireView(), snackbarMessage, Snackbar.LENGTH_SHORT).show()
                }
                is ApiEvent.OnFailed -> {
                    Snackbar.make(requireContext(), requireView(), "Update status pembelian gagal, mohon coba lagi!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun showAlertDialog(
        title: String,
        negativeButtonTitle: String,
        positiveButtonTitle: String,
        onPositiveButtonClick: () -> Unit
    ) {
        val alertDialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setNegativeButton(negativeButtonTitle) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(positiveButtonTitle) { dialog, _ ->
                onPositiveButtonClick()
                dialog.dismiss()
            }
            .create()
        return alertDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        sealed class StatusBeliProduk(val status: String) {
            class Cancel : StatusBeliProduk("dibatalkan")
            class Finish : StatusBeliProduk("selesai")
        }
    }

}