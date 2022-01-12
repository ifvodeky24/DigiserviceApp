package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.servicehp.ServiceHandphoneByCustomerGetAll
import com.example.core_data.domain.servicehp.ServiceHandphoneByTechnicianGetAll
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentServiceHandphoneCustomerBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceHandphoneCustomerFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentServiceHandphoneCustomerBinding? = null
    private val binding: FragmentServiceHandphoneCustomerBinding get() = _binding!!

    private val serviceHandphoneViewModel: ServiceHandphoneViewModel by viewModel()

    private lateinit var serviceHandphoneByCustomer: ServiceHandphoneByCustomerGetAll

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceHandphoneCustomerBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceHandphoneByCustomer = ServiceHandphoneCustomerFragmentArgs
            .fromBundle(arguments as Bundle).serviceHandphoneDetail

        setupDisplay(serviceHandphoneByCustomer)

        val isEnabled = (
            serviceHandphoneByCustomer.statusService == "diterima" ||
            serviceHandphoneByCustomer.statusService == "proses"
        )

        setupButton(isEnabled)

        binding.backImageView.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.apply {
            btnAccept.setOnClickListener(this@ServiceHandphoneCustomerFragment)
            btnReject.setOnClickListener(this@ServiceHandphoneCustomerFragment)
        }

        serviceHandphoneViewModel.isUpdateServiceHandphone.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {
                    setupButton(false)
                }
                is ApiEvent.OnSuccess -> {
                    serviceHandphoneViewModel.getServiceHandphoneById(serviceHandphoneByCustomer.serviceHandphoneId)
                    Toast.makeText(context, "Verifikasi service berhasil!", Toast.LENGTH_SHORT).show()
                }
                is ApiEvent.OnFailed -> {
                    setupButton(true)
                    Toast.makeText(context, "Versikasi service gagal, silahkan coba lagi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupButton(isEnabled: Boolean) {
        binding.apply {
            btnAccept.isEnabled = isEnabled
            btnReject.isEnabled = isEnabled
        }
    }

    private fun setupDisplay(serviceHandphoneByTechnician: ServiceHandphoneByCustomerGetAll) {
        Timber.d(serviceHandphoneByTechnician.toString())
        binding.apply {
            tvTeknisiName.text = serviceHandphoneByTechnician.teknisiNama
            tvTeknisiEmail.text = serviceHandphoneByTechnician.email
            tvTeknisiHpName.text = serviceHandphoneByTechnician.teknisiHp
            tvTeknisiHpName.text = serviceHandphoneByTechnician.jenisHp
            tvTeknisiHpDamageName.text = serviceHandphoneByTechnician.jenisKerusakan
            tvServiceHpCreated.text = serviceHandphoneByTechnician.createdAt
            tvServiceHpStatus.text = serviceHandphoneByTechnician.statusService

            val isKurir = when (serviceHandphoneByTechnician.byKurir) {
                1 -> R.id.kurir_yes
                else -> R.id.kurir_no
            }

            byKurir.check(isKurir)

            Glide.with(this@ServiceHandphoneCustomerFragment)
                .load(serviceHandphoneByTechnician.teknisiFoto)
                .into(ivTeknisiPhoto)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_accept -> {
                serviceHandphoneViewModel
                    .updateServiceHandphone(
                        serviceHandphoneByCustomer.serviceHandphoneId,
                        "selesai"
                    )
            }
            R.id.btn_reject -> {
                serviceHandphoneViewModel
                    .updateServiceHandphone(
                        serviceHandphoneByCustomer.serviceHandphoneId,
                        "dibatalkan"
                    )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}