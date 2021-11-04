package com.example.feature_auth.choose

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.JenisKerusakan
import com.example.core_data.domain.ListJenisKerusakan
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentChooseBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChooseFragment : Fragment() {

    private var _binding: FragmentChooseBinding? = null
    private val binding get() = _binding!!

    private val chooseViewModel: ChooseViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentChooseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeData()
    }

    private fun observeData() {
        chooseViewModel.jenisHp()
        chooseViewModel.liveJenisHp.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                //is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    setupRecycler(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
        chooseViewModel.jenisKerusakan()
        chooseViewModel.liveJenisKerusakan.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                //is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    setupRecyclerJenisKeruskan(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    //hideProgress(true)
                }
            }
        })
    }

    private fun setupRecyclerJenisKeruskan(listJenisKerusakan: ListJenisKerusakan) {
        binding.rvPhoneType.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listJenisKerusakan))
            withItem<JenisKerusakan, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.text = item.namaKerusakan
                }
            }
        }
    }

    private fun setupRecycler(listJenisHp: List<JenisHp>) {
        binding.rvSkills.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listJenisHp))
            withItem<JenisHp, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.text = item.jenisNama
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}