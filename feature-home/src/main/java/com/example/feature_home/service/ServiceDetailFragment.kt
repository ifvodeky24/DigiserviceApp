package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
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
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentServiceDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceDetailFragment : Fragment() {

    private var _binding: FragmentServiceDetailBinding? = null
    private val binding get() = _binding!!

    private val args: ServiceDetailFragmentArgs by navArgs()

    private val accountViewModel: AccountViewModel by viewModel()

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