package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.technician.NearbyTechnician
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_resource.showApiFailedDialog
import com.example.feature_home.HomeViewModel
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentServiceCustomerBinding
import com.example.feature_home.viewHolder.ItemServiceViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceCustomerFragment : Fragment() {

    private var _binding: FragmentServiceCustomerBinding? = null
    private val binding: FragmentServiceCustomerBinding
        get() = _binding as FragmentServiceCustomerBinding

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceCustomerBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun observeNearbyTechnician() {
        homeViewModel.findNearbyTechnicianResponse.observe(
            viewLifecycleOwner,
            { findNearbyTechnician ->
                when (findNearbyTechnician) {
                    is ApiEvent.OnProgress -> {
                    }
                    is ApiEvent.OnSuccess -> findNearbyTechnician.getData()?.let {
                        onDataFindNearbyTechnicianLoaded(findNearbyTechnician.getData()!!)
                    }
                    is ApiEvent.OnFailed -> if (!findNearbyTechnician.hasNotBeenConsumed) {
                        showApiFailedDialog(findNearbyTechnician.getException())
                    }
                }
            })
    }

    private fun onDataServiceReceive(data: List<TechnicianGetAll>) {
        binding.rvServiceTerdekat.setup {
            withDataSource(dataSourceTypedOf(data))
            withItem<TechnicianGetAll, ItemServiceViewHolder>(R.layout.item_service) {
                onBind(::ItemServiceViewHolder) { _, item ->
                    tvServiceName.text = item.teknisiNama
                    Glide
                        .with(requireActivity())
                        .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
                        .centerCrop()
                        .into(ivService)
                }

//                onClick {
//                    val itemGetAll = TechnicianGetAll(
//                        teknisiId = item.teknisiId,
//                        email = item.email,
//                        teknisiNama = item.teknisiNama,
//                        teknisiNamaToko = item.teknisiNamaToko,
//                        teknisiAlamat = item.teknisiAlamat,
//                        teknisiLat = item.teknisiLat,
//                        teknisiLng = item.teknisiLng,
//                        teknisiHp = item.teknisiHp,
//                        createdAt = item.createdAt,
//                        updatedAt = item.updatedAt,
//                        teknisiTotalScore = item.teknisiTotalScore,
//                        teknisiTotalResponden = item.teknisiTotalResponden,
//                        teknisiDeskripsi = item.teknisiDeskripsi,
//                        teknisiFoto = item.teknisiFoto,
//                        teknisiSertifikat = item.teknisiSertifikat
//                    )
//                    val directionTechnicianGetAll =
//                        HomeFragmentDirections.actionHomeFragmentToServiceDetailFragment(
//                            itemGetAll
//                        )
//                    findNavController().navigate(directionTechnicianGetAll)
//                }
            }
        }
    }

    private fun onDataFindNearbyTechnicianLoaded(data: List<NearbyTechnician>) {
//        binding.rvServiceTerdekat.setup {
//            withDataSource(dataSourceTypedOf(filter))
//            withItem<NearbyTechnician, ItemNearbyViewHolder>(R.layout.item_teknisi_terdekat) {
//                onBind(::ItemNearbyViewHolder) { _, item ->
//                    tvTeknisiName.text = item.teknisiNama
//                    Glide
//                        .with(requireActivity())
//                        .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
//                        .centerCrop()
//                        .into(ivTeknisi)
//                }
//
//                onClick {
//                    val itemGetAll = TechnicianGetAll(
//                        teknisiId = item.teknisiId,
//                        email = item.email,
//                        teknisiNama = item.teknisiNama,
//                        teknisiNamaToko = item.teknisiNamaToko,
//                        teknisiAlamat = item.teknisiAlamat,
//                        teknisiLat = item.teknisiLat,
//                        teknisiLng = item.teknisiLng,
//                        teknisiHp = item.teknisiHp,
//                        createdAt = item.createdAt,
//                        updatedAt = item.updatedAt,
//                        teknisiTotalScore = item.teknisiTotalScore,
//                        teknisiTotalResponden = item.teknisiTotalResponden,
//                        teknisiDeskripsi = item.teknisiDeskripsi,
//                        teknisiFoto = item.teknisiFoto,
//                        teknisiSertifikat = item.teknisiSertifikat
//                    )
//                    val directionTechnicianGetAll =
//                        HomeFragmentDirections.actionHomeFragmentToServiceDetailFragment(
//                            itemGetAll
//                        )
//                    findNavController().navigate(directionTechnicianGetAll)
//                }
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}