package com.example.feature_service.service_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.RequestAddServiceHandphone
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.feature_service.R
import com.example.feature_service.databinding.OrderTechnicianCustomerDialogBinding
import com.example.feature_service.service_detail.ServiceDetailViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class OrderTechicianCustomerDialog : DialogFragment() {

    private var _binding: OrderTechnicianCustomerDialogBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: ServiceDetailViewModel by viewModel()
    private val orderTechnicianViewModel: OrderTechnicianViewModel by viewModel()

    private val serviceHandphoneRequest by lazy { RequestAddServiceHandphone() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = OrderTechnicianCustomerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val technician = arguments?.getParcelable<TechnicianGetAll>(KEY_TECHNICIAN)
        val kurir = arguments?.getInt(KEY_BY_KURIR) as Int
        val listJenisHp = arguments?.getStringArrayList(KEY_LIST_JENIS_HP)
        val listSkills = arguments?.getStringArrayList(KEY_LIST_SKILLS)

        setupJenisHp(listJenisHp)
        setupSkills(listSkills)


        observeCurrentTeknisi(technician)
        observeCurrentUser()
        observeBuatPesanan()

        binding.btnPesanService.setOnClickListener {

            arrayOf(
                binding.tieJenisHp,
                binding.tieJenisKerusakan,
                binding.tieDeskripsiKerusakan
            ).forEach { digiEditText ->
                if (digiEditText.text?.isEmpty() as Boolean) {
                    digiEditText.error = "Field ini tidak boleh kosong"
                    return@setOnClickListener
                }
            }

            serviceHandphoneRequest.apply {
                jenisHp = binding.tieJenisHp.text.toString()
                jenisKerusakan = binding.tieJenisKerusakan.text.toString()
                deskripsiKerusakan = binding.tieDeskripsiKerusakan.text.toString()
                byKurir = kurir
            }

            orderTechnicianViewModel.insertServiceHandphone(
                teknisiId = serviceHandphoneRequest.teknisiId,
                pelangganId = serviceHandphoneRequest.pelangganId,
                jenisHp = serviceHandphoneRequest.jenisHp,
                jenisKerusakan = serviceHandphoneRequest.jenisKerusakan,
                deskripsiKerusakan = serviceHandphoneRequest.deskripsiKerusakan,
                byKurir = serviceHandphoneRequest.byKurir
            )
        }
    }

    private fun observeCurrentUser() {
        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            if (auth != null) {
                serviceHandphoneRequest.pelangganId = auth.pelangganId
            }
        })
    }

    private fun observeCurrentTeknisi(technician: TechnicianGetAll?) {
        if (technician != null) {
            serviceHandphoneRequest.teknisiId = technician.teknisiId
        }
    }

    private fun observeBuatPesanan() {
        orderTechnicianViewModel.isSaveForm.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    activity?.onBackPressed()
                    Toast.makeText(context, "Berhasil membuat pesanan", Toast.LENGTH_LONG).show()
                    Timber.d(serviceHandphoneRequest.toString())
                }
                is ApiEvent.OnFailed -> {
                    Timber.d("Failed ${event.getException()}" )
                }
            }
        }
    }

    private fun setupJenisHp(listJenisHp: List<String>?) {
        listJenisHp?.let {
            val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, it)
            binding.tieJenisHp.setAdapter(adapter)
        }
    }

    private fun setupSkills(listSkills: List<String>?) {
        listSkills?.let {
            val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, it)
            binding.tieJenisKerusakan.setAdapter(adapter)
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    companion object {
        const val TAG = "OrderTechician"
        const val KEY_TECHNICIAN = "key_technician"
        const val KEY_BY_KURIR = "key_by_kurir"
        const val KEY_LIST_JENIS_HP = "key_list_jenis_hp"
        const val KEY_LIST_SKILLS = "key_list_skills"

        fun newInstance(
            technician: TechnicianGetAll?,
            byKurir: Int,
            listJenisHp: ArrayList<String>,
            listSkills: ArrayList<String>
        ): OrderTechicianCustomerDialog {
            val args = Bundle().apply {
                putParcelable(KEY_TECHNICIAN, technician)
                putInt(KEY_BY_KURIR, byKurir)
                putStringArrayList(KEY_LIST_JENIS_HP, listJenisHp)
                putStringArrayList(KEY_LIST_SKILLS, listSkills)
            }
            val fragment = OrderTechicianCustomerDialog().apply {
                arguments = args
            }

            return fragment
        }
    }
}