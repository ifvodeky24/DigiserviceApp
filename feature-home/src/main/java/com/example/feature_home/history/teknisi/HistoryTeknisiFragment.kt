package com.example.feature_home.history.teknisi

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.servicehp.ServiceHandphoneByTechnicianGetAll
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentHistoryTeknisiBinding
import com.example.feature_home.service.ServiceHandphoneViewModel
import com.example.feature_home.viewHolder.ItemHistoryServiceHandphoneCustomer
import com.example.feature_home.viewHolder.ItemHistoryServiceHandphoneTechnician
import org.koin.androidx.viewmodel.ext.android.viewModel

class HistoryTeknisiFragment : Fragment() {

    private var _binding: FragmentHistoryTeknisiBinding? = null
    private val binding: FragmentHistoryTeknisiBinding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()
    private val serviceHandphoneViewModel: ServiceHandphoneViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHistoryTeknisiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observerUser()

        serviceHandphoneViewModel.serviceHandphoneByTechnician.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    onDataHistoryServiceLoaded(event.getData())
                }
                is ApiEvent.OnFailed -> {}
            }
        }

    }

    private fun observerUser() {
        accountViewModel.authUser.observe(viewLifecycleOwner) { auth ->
            if (auth != null) {
                observerHistoryService(auth.teknisiId)
            }
        }
    }

    private fun observerHistoryService(technicianId: Int) {
        serviceHandphoneViewModel.getServiceHandphoneHistoryByTechnicianId(technicianId)
    }

    private fun onDataHistoryServiceLoaded(data: List<ServiceHandphoneByTechnicianGetAll>?) {
        if (data != null) {
            binding.rvHistoryTeknisi.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<ServiceHandphoneByTechnicianGetAll, ItemHistoryServiceHandphoneTechnician>(R.layout.item_history_service_handphone_technician) {
                    onBind(::ItemHistoryServiceHandphoneTechnician) { _, item ->
                        val customerName = item.pelangganNama.run {
                            if (length >= 18) {
                                "${this.slice(0..16)}..."
                            } else {
                                this
                            }
                        }
                        tvServiceHpCustomerName.text = customerName
                        tvServiceHpStatus.text = item.statusService
                        tvServiceHpDate.text = item.createdAt
                        tvServiceHpType.text = item.jenisHp
                        tvServiceHpDamageType.text = item.jenisKerusakan

                        Glide.with(this@HistoryTeknisiFragment)
                            .load(item.pelangganFoto)
                            .transform(CenterCrop())
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