package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.servicehp.ServiceHandphoneByTechnicianGetAll
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentServiceHandphoneTechnicianBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceHandphoneTechnicianFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentServiceHandphoneTechnicianBinding? = null
    private val binding: FragmentServiceHandphoneTechnicianBinding get() = _binding!!

    private val serviceHandphoneViewModel: ServiceHandphoneViewModel by viewModel()

    private lateinit var serviceHandphoneByTechnician: ServiceHandphoneByTechnicianGetAll

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceHandphoneTechnicianBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        serviceHandphoneByTechnician = ServiceHandphoneTechnicianFragmentArgs
            .fromBundle(arguments as Bundle).serviceHandphoneDetail

        serviceHandphoneViewModel.getServiceHandphoneById(serviceHandphoneByTechnician.serviceHandphoneId)

        observer()

        val isEnabled = serviceHandphoneByTechnician.statusService == "proses"
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
                    serviceHandphoneViewModel.getServiceHandphoneById(serviceHandphoneByTechnician.serviceHandphoneId)
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
        serviceHandphoneViewModel.serviceHandphoneByById.observe(viewLifecycleOwner) { event ->
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

    private fun setupDisplay(serviceHandphoneByTechnician: ServiceHandphoneByTechnicianGetAll) {
        Timber.d(serviceHandphoneByTechnician.toString())
        binding.apply {
            tvCustomerName.text = serviceHandphoneByTechnician.pelangganNama
            tvCustomerEmail.text = serviceHandphoneByTechnician.email
            tvCustomerNoHp.text = serviceHandphoneByTechnician.pelangganHp
            tvCustomerHpName.text = serviceHandphoneByTechnician.jenisHp
            tvCustomerHpDamageName.text = serviceHandphoneByTechnician.jenisKerusakan
            tvServiceHpCreated.text = serviceHandphoneByTechnician.createdAt
            tvServiceHpStatus.text = serviceHandphoneByTechnician.statusService

            val isKurir = when (serviceHandphoneByTechnician.byKurir) {
                1 -> R.id.kurir_yes
                else -> R.id.kurir_no
            }

            byKurir.check(isKurir)

            Glide.with(this@ServiceHandphoneTechnicianFragment)
                .load(serviceHandphoneByTechnician.pelangganFoto)
                .into(ivCustomerPhoto)
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_accept -> {
                serviceHandphoneViewModel.updateServiceHandphone(serviceHandphoneByTechnician.serviceHandphoneId, "diterima")
            }
            R.id.btn_reject -> {
                serviceHandphoneViewModel.updateServiceHandphone(serviceHandphoneByTechnician.serviceHandphoneId, "ditolak")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}