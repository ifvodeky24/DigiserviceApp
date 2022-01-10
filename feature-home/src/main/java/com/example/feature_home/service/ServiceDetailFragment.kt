package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentServiceDetailBinding
import com.example.feature_home.store.ProductViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceDetailFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentServiceDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ServiceDetailFragmentArgs by navArgs()

    private val accountViewModel: AccountViewModel by viewModel()

    private lateinit var preferenceManager : PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServiceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        preferenceManager = PreferenceManager(requireActivity())

        setHasOptionsMenu(true)

        setupDisplay()

        setupObserver()

        binding.btnOrder.setOnClickListener {
            val byKurir = if (binding.kurirYes.isChecked) 1 else 0
            OrderTechicianDialog.newInstance(args.technician, byKurir).show(childFragmentManager, OrderTechicianDialog.TAG)
        }

        binding.backImageView.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    private fun setupObserver() {
        accountViewModel.liveSkils.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    setupRecyclerSkils(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
    }

    private fun setupDisplay() {
        with(args.technician){
            if (this?.teknisiFoto?.isNotEmpty() == true) binding.ivStore.load(
                APP_TEKNISI_IMAGES_URL+teknisiFoto
            ){
                crossfade(true)
            }
            binding.tvRatingCount.text = String.format("%.1f", (this?.teknisiTotalScore?.div(teknisiTotalResponden)))
            binding.tvStoreName.text = this?.teknisiNamaToko ?: ""
            binding.tvName.text = this?.teknisiNama ?: ""
            binding.tvNoHp.text = this?.teknisiHp ?: ""
            binding.tvEmail.text = this?.email
            binding.tvStoreAddress.text = this?.teknisiAlamat ?: ""
            binding.tvStoreDesc.text = this?.teknisiDeskripsi
            accountViewModel.setCurrentSkill(this?.teknisiId ?: 0)

            binding.chatButton.setOnClickListener {
                Timber.d("sdsds ${this?.teknisiId}")
                val database = FirebaseFirestore.getInstance()
                database.collection(Constants.KEY_COLLECTION_USERS)
                    .whereEqualTo("teknisiId", this?.teknisiId)
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