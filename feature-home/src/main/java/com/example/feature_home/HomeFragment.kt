package com.example.feature_home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.technician.NearbyTechnician
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.feature_home.databinding.FragmentHomeBinding
import com.example.feature_home.viewHolder.ItemNearbyViewHolder
import com.example.feature_home.viewHolder.ItemPopulerViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        setHasOptionsMenu(true)

        homeViewModel.technicianGetAll()
        homeViewModel.findNearbyTechnician("1","1")

        observeTechnicianGetAll()
        observeNearbyTechnician()

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    Toast.makeText(requireActivity(), "search", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }


    private fun observeNearbyTechnician() {
        homeViewModel.findNearbyTechnicianResponse.observe(viewLifecycleOwner, { findNearbyTechnician ->
            when (findNearbyTechnician) {
                is ApiEvent.OnProgress -> {
                }
                is ApiEvent.OnSuccess -> findNearbyTechnician.getData()?.let {
                    onDataFindNearbyTechnicianLoaded(findNearbyTechnician.getData()!!)
                    Timber.d(" uiuiuiui ${findNearbyTechnician.getData()}")
                }
                is ApiEvent.OnFailed -> if (!findNearbyTechnician.hasNotBeenConsumed) {
                    Timber.d(" booom ${findNearbyTechnician.getException()}")
                }
            }
        })
    }

    private fun onDataFindNearbyTechnicianLoaded(data: List<NearbyTechnician>) {
        if (data.isNotEmpty()) {
            binding.rvTerdekat.setup{
                withDataSource(dataSourceTypedOf(data))
                withItem<NearbyTechnician, ItemNearbyViewHolder>(R.layout.item_teknisi_terdekat){
                    onBind(::ItemNearbyViewHolder){ index, item ->
                        tvTeknisiName.text = item.teknisiNama
                       Glide
                            .with(requireActivity())
                            .load(item.teknisiFoto)
                            .centerCrop()
//                            .placeholder(R.drawable.loading_spinner)
                            .into(ivTeknisi);
                    }
                }
            }
        }
    }

    private fun observeTechnicianGetAll() {
        homeViewModel.technicianGetAllResponse.observe(viewLifecycleOwner, { technicianGetAll ->
            when (technicianGetAll) {
                is ApiEvent.OnProgress -> {
                }
                is ApiEvent.OnSuccess -> technicianGetAll.getData()?.let {
                    onDataTechnicianGetAllLoaded(technicianGetAll.getData()!!)
                }
                is ApiEvent.OnFailed -> if (!technicianGetAll.hasNotBeenConsumed) {
                    Timber.d(" booom ${technicianGetAll.getException()}")
                }
            }
        })
    }

    private fun onDataTechnicianGetAllLoaded(data: List<TechnicianGetAll>) {
        if (data.isNotEmpty()) {
            binding.rvTerpopuler.setup{
                withDataSource(dataSourceTypedOf(data))
                withItem<TechnicianGetAll, ItemPopulerViewHolder>(R.layout.item_teknisi_terpopuler){
                    onBind(::ItemPopulerViewHolder){ index, item ->
                        tvTeknisiName.text = item.teknisiNama
                        tvRating.text = String.format("%.1f", (item.teknisiTotalScore/item.teknisiTotalResponden)).toDouble().toString()
                        Glide
                            .with(requireActivity())
                            .load(item.teknisiFoto)
                            .centerCrop()
//                            .placeholder(R.drawable.loading_spinner)
                            .into(ivTeknisi);
                    }
                }
            }
        }
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        inflater.inflate(R.menu.menu_home, menu);
//
//        super.onCreateOptionsMenu(menu, inflater)
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Toast.makeText(requireActivity(), item.title, Toast.LENGTH_SHORT).show()
//        return super.onOptionsItemSelected(item)
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}