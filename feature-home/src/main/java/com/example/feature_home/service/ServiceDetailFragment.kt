package com.example.feature_home.service

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.feature_home.HomeViewModel
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.account.SkillsItemViewHolder
import com.example.feature_home.databinding.FragmentServiceDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ServiceDetailFragment : Fragment() {

    private var _binding: FragmentServiceDetailBinding? = null
    private val binding get() = _binding!!

    val args: ServiceDetailFragmentArgs by navArgs()

    private val accountViewModel: AccountViewModel by viewModel()
    private val homeViewModel: HomeViewModel by viewModel()
    private val serviceViewModel: ServiceViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentServiceDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        setHasOptionsMenu(true)

        Toast.makeText(requireContext(), "${args.technician}", Toast.LENGTH_SHORT).show()
        with(args.technician){
            if (teknisiFoto?.isNotEmpty() == true) binding.userImageView.load(
                teknisiFoto
            ){
                crossfade(true)
                transformations(CircleCropTransformation())
            }
            binding.tvRatingCount.text = String.format("%.1f", (teknisiTotalScore/teknisiTotalResponden)).toDouble().toString()
            binding.tvStoreName.text = teknisiNamaToko
            binding.tvStoreEmail.text = email
            binding.tvStoreAddress.text = teknisiAlamat
            binding.tvStoreDesc.text = teknisiDeskripsi
            accountViewModel.setCurrentSkill(teknisiId ?: 0)

        }

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

    private fun setupRecyclerSkils(listSkils: ResultSkils) {
        binding.rvJenisKerusakanHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.skils))
            withItem<Skils, SkillsItemViewHolder>(R.layout.item_skils){
                onBind(::SkillsItemViewHolder){ _, item ->
                    titleSkils.text = "- ${item.namaKerusakan}"
                }
            }
        }
        binding.rvJenisHp.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.jenisHp))
            withItem<JenisHp, SkillsItemViewHolder>(R.layout.item_skils){
                onBind(::SkillsItemViewHolder){ _, item ->
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