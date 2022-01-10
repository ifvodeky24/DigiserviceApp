package com.example.feature_service.service_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_SERTIFIKAT_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.APP_TEMPAT_USAHA_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_service.service_dialog.OrderTechicianCustomerDialog
import com.example.feature_service.R
import com.example.feature_service.databinding.FragmentServiceDetailCustomerBinding
import com.example.feature_service.viewHolder.SkillsItemSecondaryViewHolder
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceDetailCustomerFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentServiceDetailCustomerBinding? = null
    private val binding: FragmentServiceDetailCustomerBinding
        get() = _binding as FragmentServiceDetailCustomerBinding

    private val args: ServiceDetailCustomerFragmentArgs by navArgs()
    private val serviceDetailViewModel: ServiceDetailViewModel by viewModel()

    private lateinit var preferenceManager : PreferenceManager

    private val listSkills = arrayListOf<String>()
    private val listHp = arrayListOf<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceDetailCustomerBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireActivity())

        setupDisplay()
        setupObserver()

        binding.btnOrder.setOnClickListener {
            val byKurir = if (binding.kurirYes.isChecked) 1 else 0
            OrderTechicianCustomerDialog.newInstance(args.technician, byKurir, listHp, listSkills)
                .show(childFragmentManager, OrderTechicianCustomerDialog.TAG)
        }

        binding.backImageView.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupObserver() {
        serviceDetailViewModel.liveSkils.observe(viewLifecycleOwner, { event ->
            when(event) {
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    val jenisHp = it.jenisHp.map { it.jenisNama }
                    val skills = it.skils.map { it.namaKerusakan }
                    listHp.apply {
                        clear()
                        addAll(jenisHp)
                    }
                    listSkills.apply {
                        clear()
                        addAll(skills)
                    }

                    setupRecyclerSkils(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) {
                    //hideProgress(true)
                }
            }
        })
    }

    private fun setupDisplay() {
        with(args.technician){
            if (this.teknisiFoto.isNotEmpty()) {
                Glide.with(this@ServiceDetailCustomerFragment)
                    .load(APP_TEKNISI_IMAGES_URL + teknisiFoto)
                    .into(binding.ivStore)
            }

            if (this.teknisiSertifikat.isNotEmpty()) {
                Glide.with(this@ServiceDetailCustomerFragment)
                    .load(APP_SERTIFIKAT_IMAGES_URL + teknisiSertifikat)
                    .into(binding.ivServiceSertifikat)
            }

            if (this.teknisiTempatUsaha.isNotEmpty()) {
                Glide.with(this@ServiceDetailCustomerFragment)
                    .load(APP_TEMPAT_USAHA_IMAGES_URL + teknisiTempatUsaha)
                    .into(binding.ivTempatService)
            }

            binding.tvRatingCount.text = String.format("%.1f", (this.teknisiTotalScore.div(teknisiTotalResponden)))
            binding.tvStoreName.text = this.teknisiNamaToko ?: ""
            binding.tvName.text = this.teknisiNama ?: ""
            binding.tvNoHp.text = this.teknisiHp ?: ""
            binding.tvEmail.text = this.email
            binding.tvStoreAddress.text = this.teknisiAlamat ?: ""
            binding.tvStoreDesc.text = this.teknisiDeskripsi
            serviceDetailViewModel.setCurrentSkill(this.teknisiId ?: 0)

            binding.chatButton.setOnClickListener {
                Timber.d("sdsds ${this.teknisiId}")
                val database = FirebaseFirestore.getInstance()
                database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo("teknisiId", this.teknisiId)
                    .get()
                    .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                        if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                            val documentSnapshot = task.result!!.documents[0]
                            preferenceManager.putString(Constants.KEY_RECEIVER_ID, documentSnapshot.id)
                            preferenceManager.putString(Constants.KEY_RECEIVER_NAME, documentSnapshot.getString("name"))
                            preferenceManager.putString(Constants.KEY_RECEIVER_PHOTO, documentSnapshot.getString("foto"))

                            navigateToChatActivity(finnishCurrent = true, status = "2")
                        } else {
                            Timber.d("gagal")
                            Toast.makeText(requireContext(), "Pengguna ini tidak dapat melakukan chat", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
    }

    private fun setupRecyclerSkils(listSkils: ResultSkils) {
        binding.rvJenisKerusakanHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.skils))
            withItem<Skils, SkillsItemSecondaryViewHolder>(R.layout.item_skils_secondary){
                onBind(::SkillsItemSecondaryViewHolder){ _, item ->
                    titleSkils.text = "- ${item.namaKerusakan}"
                }
            }
        }
        binding.rvJenisHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.jenisHp))
            withItem<JenisHp, SkillsItemSecondaryViewHolder>(R.layout.item_skils_secondary){
                onBind(::SkillsItemSecondaryViewHolder){ _, item ->
                    titleSkils.text = "- ${item.jenisNama}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}