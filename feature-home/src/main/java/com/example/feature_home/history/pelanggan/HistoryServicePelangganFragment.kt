package com.example.feature_home.history.pelanggan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.servicehp.ServiceHandphoneByCustomerGetAll
import com.example.core_resource.showApiFailedDialog
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentHistoryServicePelangganBinding
import com.example.feature_home.service.ServiceHandphoneViewModel
import com.example.feature_home.viewHolder.ItemHistoryServiceHandphoneCustomer
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryServicePelangganFragment : Fragment() {

    private var _binding: FragmentHistoryServicePelangganBinding? = null
    private val binding: FragmentHistoryServicePelangganBinding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()
    private val serviceHandphoneViewModel: ServiceHandphoneViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryServicePelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerUser()

        serviceHandphoneViewModel.serviceHandphoneByCustomer.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    onDataHistoryServiceLoaded(event.getData())
                }
                is ApiEvent.OnFailed -> {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }

    private fun observerUser() {
        accountViewModel.authUser.observe(viewLifecycleOwner) { auth ->
            if (auth != null) {
                observerHistoryService(auth.pelangganId)
            }
        }
    }

    private fun observerHistoryService(customerId: Int) {
        serviceHandphoneViewModel.getServiceHandphoneByCustomerId(customerId)
    }

    private fun onDataHistoryServiceLoaded(data: List<ServiceHandphoneByCustomerGetAll>?) {
        if (data != null) {
            binding.rvHistoryServicePelanggan.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<ServiceHandphoneByCustomerGetAll, ItemHistoryServiceHandphoneCustomer>(R.layout.item_history_service_handphone_customer) {
                    onBind(::ItemHistoryServiceHandphoneCustomer) { _, item ->
                        val storeName = item.teknisiNamaToko.run {
                            if (length >= 18) {
                                "${this.slice(0..16)}..."
                            } else {
                                this
                            }
                        }
                        tvServiceHpStoreName.text = storeName
                        tvServiceHpTechnianName.text = item.teknisiNama
                        tvServiceHpStatus.text = item.statusService
                        tvServiceHpDate.text = item.createdAt
                        tvServiceHpType.text = item.jenisHp
                        tvServiceHpDamageType.text = item.jenisKerusakan

                        Glide.with(this@HistoryServicePelangganFragment)
                            .load(item.teknisiFoto)
                            .transform(CircleCrop())
                            .into(ivServiceHpUserPhoto)
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