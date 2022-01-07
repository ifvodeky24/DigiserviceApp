package com.example.feature_service

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
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.feature_service.databinding.FragmentSeeAllServiceBinding
import com.example.feature_service.viewHolder.ItemServiceViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SeeAllServiceFragment : Fragment() {

    private var _binding: FragmentSeeAllServiceBinding? = null
    private val binding: FragmentSeeAllServiceBinding
        get() = _binding as FragmentSeeAllServiceBinding

    private val serviceViewModel: ServiceViewModel by viewModel()

    private var teknisiId = 0
    private var level = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSeeAllServiceBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceViewModel.technicianGetAll()

        observeAuth()
        observeServiceGetAll()
    }

    private fun observeAuth() {
        serviceViewModel.auth.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                if (data.isLogin) {
                    data.let {
                        level = it.level
                        teknisiId = it.teknisiId
                    }
                }
            }
        }
    }


    private fun observeServiceGetAll() {
        serviceViewModel.technicianGetAllResponse.observe(viewLifecycleOwner, { technicianGetAll ->
            when (technicianGetAll) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> technicianGetAll.getData()?.let {
                    Timber.d(" vuvvvuu ${technicianGetAll.getData()}")
                    onDataServiceReceive(technicianGetAll.getData()!!)
//                    binding.shimmerHome.hideShimmer()
                }
                is ApiEvent.OnFailed -> if (!technicianGetAll.hasNotBeenConsumed) {
                    Timber.d(" booom ${technicianGetAll.getException()}")
//                    binding.shimmerHome.hideShimmer()
                }
            }
        })
    }

    private fun onDataServiceReceive(data: List<TechnicianGetAll>) {
        val filter =
            if (level == "teknisi") data.filter { it.teknisiId.toString() != teknisiId.toString() } else data
        if (filter.isNotEmpty()) {
            binding.rvServiceSeeAll.setup {
                withDataSource(dataSourceTypedOf(filter))
                withItem<TechnicianGetAll, ItemServiceViewHolder>(R.layout.item_service) {
                    onBind(::ItemServiceViewHolder) { _, item ->
                        tvServiceName.text = item.teknisiNama
                        Glide
                            .with(requireActivity())
                            .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
                            .centerCrop()
                            .into(ivService)
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