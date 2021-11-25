package com.example.feature_home.service

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.RequestUpdateServiceHandphone
import com.example.core_data.domain.servicehp.ServiceHandphoneTechnicianGetAll
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentServiceHandphoneTechnicianBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceHandphoneTechnicianFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentServiceHandphoneTechnicianBinding? = null
    private val binding: FragmentServiceHandphoneTechnicianBinding get() = _binding!!

    private val serviceHandphoneViewModel: ServiceHandphoneViewModel by viewModel()

    private lateinit var serviceHandphoneTechnician: ServiceHandphoneTechnicianGetAll

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceHandphoneTechnicianBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        serviceHandphoneTechnician = ServiceHandphoneTechnicianFragmentArgs
            .fromBundle(arguments as Bundle).serviceHandphoneDetail

        serviceHandphoneViewModel.getServiceHandphoneById(serviceHandphoneTechnician.serviceHandphoneId)

        observer()

        val isEnabled = serviceHandphoneTechnician.statusService == "proses"
        setupButton(isEnabled)

        binding.backImageView.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.apply {
            btnAccept.setOnClickListener(this@ServiceHandphoneTechnicianFragment)
            btnReject.setOnClickListener(this@ServiceHandphoneTechnicianFragment)
        }

        serviceHandphoneViewModel.isUpdateServiceHandphone.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {
                    setupButton(false)
                }
                is ApiEvent.OnSuccess -> {
                    serviceHandphoneViewModel.getServiceHandphoneById(serviceHandphoneTechnician.serviceHandphoneId)
                    Toast.makeText(context, "Verifikasi service berhasil!", Toast.LENGTH_SHORT).show()
                }
                is ApiEvent.OnFailed -> {
                    setupButton(true)
                    Toast.makeText(context, "Versikasi service gagal, silahkan coba lagi!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observer() {
        serviceHandphoneViewModel.serviceHandphoneById.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    setupDisplay(event.getData()!!)
                }
                is ApiEvent.OnFailed -> {}
            }
        }
    }

    private fun setupButton(isEnabled: Boolean) {

        binding.apply {
            btnAccept.isEnabled = isEnabled
            btnReject.isEnabled = isEnabled
        }
    }

    private fun setupDisplay(serviceHandphoneTechnician: ServiceHandphoneTechnicianGetAll) {
        Timber.d(serviceHandphoneTechnician.toString())
        binding.apply {
            tvCustomerName.text = serviceHandphoneTechnician.pelangganNama
            tvCustomerEmail.text = serviceHandphoneTechnician.email
            tvCustomerNoHp.text = serviceHandphoneTechnician.pelangganHp
            tvCustomerHpName.text = serviceHandphoneTechnician.jenisHp
            tvCustomerHpDamageName.text = serviceHandphoneTechnician.jenisKerusakan
            tvServiceHpCreated.text = serviceHandphoneTechnician.createdAt
            tvServiceHpStatus.text = serviceHandphoneTechnician.statusService

            val isKurir = when (serviceHandphoneTechnician.byKurir) {
                1 -> R.id.kurir_yes
                else -> R.id.kurir_no
            }

            byKurir.check(isKurir)

            Glide.with(this@ServiceHandphoneTechnicianFragment)
                .load(serviceHandphoneTechnician.pelangganFoto)
                .into(ivCustomerPhoto)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_accept -> {
                serviceHandphoneViewModel.updateServiceHandphone(serviceHandphoneTechnician.serviceHandphoneId, "diterima")
            }
            R.id.btn_reject -> {
                serviceHandphoneViewModel.updateServiceHandphone(serviceHandphoneTechnician.serviceHandphoneId, "ditolak")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}