package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.core_data.domain.servicehp.ServiceHandphoneTechnicianGetAll
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentServiceHandphoneTechnicianBinding

class ServiceHandphoneTechnicianFragment : Fragment() {

    private var _binding: FragmentServiceHandphoneTechnicianBinding? = null
    private val binding: FragmentServiceHandphoneTechnicianBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceHandphoneTechnicianBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val serviceHandphoneTechnician: ServiceHandphoneTechnicianGetAll = ServiceHandphoneTechnicianFragmentArgs
            .fromBundle(arguments as Bundle).serviceHandphoneDetail

        setupDisplay(serviceHandphoneTechnician)

        binding.backImageView.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupDisplay(serviceHandphoneTechnician: ServiceHandphoneTechnicianGetAll) {
        binding.apply {
            tvCustomerName.text = serviceHandphoneTechnician.pelangganNama
            tvCustomerEmail.text = serviceHandphoneTechnician.email
            tvCustomerNoHp.text = serviceHandphoneTechnician.pelangganHp
            tvCustomerHpName.text = serviceHandphoneTechnician.jenisHp
            tvCustomerHpDamageName.text = serviceHandphoneTechnician.jenisKerusakan

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}