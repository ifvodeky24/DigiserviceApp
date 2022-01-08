package com.example.feature_service

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.RequestAddServiceHandphone
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.feature_service.databinding.OrderTechnicianCustomerDialogBinding
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

        observeData()

//        val technician = arguments?.getParcelable<TechnicianGetAll>(KEY_TECHNICIAN)
//        val kurir = arguments?.getInt(KEY_BY_KURIR) as Int

//        observeCurrentTeknisi(technician)
//
//        observeCurrentUser()

//        binding.btnCreateOrder.setOnClickListener {

//            arrayOf(binding.edtInputJenisHp, binding.edtInputKerusakanHp).forEach { digiEditText ->
//                if (digiEditText.text?.isEmpty() as Boolean) {
//                    digiEditText.error = "Field ini tidak boleh kosong"
//                    return@setOnClickListener
//                }
//            }

//            serviceHandphoneRequest.apply {
//                jenisHp = binding.edtInputJenisHp.text.toString()
//                jenisKerusakan = binding.edtInputKerusakanHp.text.toString()
//                byKurir = kurir
//            }

//            orderTechnicianViewModel.insertServiceHandphone(
//                teknisiId = serviceHandphoneRequest.teknisiId,
//                pelangganId = serviceHandphoneRequest.pelangganId,
//                jenisHp = serviceHandphoneRequest.jenisHp,
//                jenisKerusakan = serviceHandphoneRequest.jenisKerusakan,
//                byKurir = serviceHandphoneRequest.byKurir
//            )
//
//            orderTechnicianViewModel.isSaveForm.observe(viewLifecycleOwner) { event ->
//                when(event) {
//                    is ApiEvent.OnProgress -> {}
//                    is ApiEvent.OnSuccess -> {
//                        activity?.onBackPressed()
//                        Toast.makeText(context, "Berhasil membuat pesanan", Toast.LENGTH_LONG).show()
//                        Timber.d(serviceHandphoneRequest.toString())
//                    }
//                    is ApiEvent.OnFailed -> {
//                        Timber.d("Failed ${event.getException()}" )
//                    }
//                }
//            }
//        }
    }

//    private fun observeCurrentUser() {
//        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
//            if (auth != null) {
//                serviceHandphoneRequest.pelangganId = auth.pelangganId
//            }
//        })
//    }
//
//    private fun observeCurrentTeknisi(technician: TechnicianGetAll?) {
//        if (technician != null) {
//            serviceHandphoneRequest.teknisiId = technician.teknisiId
//        }
//    }
    
    private fun observeData() {
        orderTechnicianViewModel.jenisHp()
        orderTechnicianViewModel.liveJenisHp.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                // is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
//                    orderTechnicianViewModel jenisHp it
//                    setupRecycler(it)
                    val listMerek = it.map { it.jenisNama }
                    setupMerek(listMerek)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
        orderTechnicianViewModel.jenisKerusakan()
        orderTechnicianViewModel.liveJenisKerusakan.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                //is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    val listKerusakan = it.map { it.namaKerusakan }
                    setupKerusakan(listKerusakan)
//                    orderTechnicianViewModel jenisKerusakan it
//                    setupRecyclerJenisKeruskan(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
    }

    private fun setupMerek(listMerek: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, listMerek)
        binding.tieJenisHp.setAdapter(adapter)
    }

    private fun setupKerusakan(listKerusakan: List<String>) {
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown_menu, listKerusakan)
        binding.tieJenisKerusakan.setAdapter(adapter)
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

        fun newInstance(technician: TechnicianGetAll?, byKurir: Int): OrderTechicianCustomerDialog {
            val args = Bundle().apply {
                putParcelable(KEY_TECHNICIAN, technician)
                putInt(KEY_BY_KURIR, byKurir)
            }
            val fragment = OrderTechicianCustomerDialog().apply {
                arguments = args
            }

            return fragment
        }
    }
}