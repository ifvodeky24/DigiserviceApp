package com.example.feature_home.account

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
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
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentAccountBinding
import org.koin.android.ext.android.bind
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonEdit.setOnClickListener {
            findNavController().navigate(R.id.editAccountFragment)
        }

        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            if (auth?.level == "teknisi"){
                binding.labelEmail.text = auth.email
                binding.labelName.text = auth.name
                accountViewModel.setCurrentTechinicial(auth.email)

                accountViewModel.technicial.observe(viewLifecycleOwner, { tech ->
                    if (tech?.teknisiFoto?.isNotEmpty() == true) binding.imageProfilePicture.load(
                        tech.teknisiFoto
                    ){
                        crossfade(true)
                        transformations(CircleCropTransformation())
                    }
                    binding.labelStoreName.text = tech?.teknisiNamaToko
                    binding.labelPhone.text = tech?.teknisiHp
                    binding.labelDescription.text = tech?.teknisiDeskripsi
                    binding.labelAddress.text = tech?.teknisiAlamat
                    accountViewModel.setCurrentSkill(tech?.teknisiId ?: 0)
                })

                accountViewModel.technicial.observe(viewLifecycleOwner, { tech ->
                    binding.labelDescription.text = tech?.teknisiDeskripsi
                    binding.labelAddress.text = tech?.teknisiAlamat
                    accountViewModel.setCurrentSkill(tech?.teknisiId ?: 0)
                })

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
            else{
                binding.labelEmail.text = auth?.email
                binding.labelName.text = auth?.name
            }

        })
    }

    private fun setupRecyclerSkils(listSkils: ResultSkils) {
        binding.rvSkills.setup {
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
}