package com.example.feature_home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductGetAll
import com.example.core_data.domain.technician.NearbyTechnician
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_navigation.ModuleNavigator
import com.example.feature_home.databinding.FragmentHomeBinding
import com.example.feature_home.store.ProductViewModel
import com.example.feature_home.viewHolder.ItemNearbyViewHolder
import com.example.feature_home.viewHolder.ItemPopulerViewHolder
import com.example.feature_home.viewHolder.ItemProductViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private val productViewModel: ProductViewModel by viewModel()

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

//        setHasOptionsMenu(true)

        homeViewModel.technicianGetAll()
        homeViewModel.findNearbyTechnician("1", "1")
        productViewModel.productGetAll()

        observeProductGetAll()
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

    private fun observeProductGetAll() {
        productViewModel.productGetAllResponse.observe(viewLifecycleOwner) { productAll ->
            when (productAll) {
                is ApiEvent.OnProgress -> {
                }
                is ApiEvent.OnSuccess -> productAll.getData().let {
                    onDataProductAllLoaded(productAll.getData()!!)
                    Timber.d(" uiuiuiui ${productAll.getData()}")
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" booom ${productAll.getException()}")
                }
            }
        }
    }

    private fun onDataProductAllLoaded(data: List<ProductGetAll>) {
        if (data.isNotEmpty()) {
            binding.rvTerbaru.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<ProductGetAll, ItemProductViewHolder>(R.layout.item_produk_terbaru) {
                    onBind(::ItemProductViewHolder) { _, item ->
                        tvProductName.text = item.jualJudul
                        tvProductDesciption.text = item.jualDeskripsi
                        Glide.with(requireActivity())
                            .load(APP_PRODUCT_IMAGES_URL+item.pathPhoto)
                            .centerCrop()
                            .into(ivProductPhoto)
                    }

                    onClick {
                        navigateToProductActivity(item.jualId.toString())
                    }
                }
            }
        }
    }

    private fun observeNearbyTechnician() {
        homeViewModel.findNearbyTechnicianResponse.observe(
            viewLifecycleOwner,
            { findNearbyTechnician ->
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
        Timber.d(" uiuiuiui 1 $data")
        if (data.isNotEmpty()) {
            Timber.d(" uiuiuiui 2 $data")
            binding.rvTerdekat.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<NearbyTechnician, ItemNearbyViewHolder>(R.layout.item_teknisi_terdekat) {
                    onBind(::ItemNearbyViewHolder) { _, item ->
                        tvTeknisiName.text = item.teknisiNama
                        Glide
                            .with(requireActivity())
                            .load(item.teknisiFoto)
                            .centerCrop()
//                            .placeholder(R.drawable.loading_spinner)
                            .into(ivTeknisi);
                    }

                    onClick {
                        val itemGetAll = TechnicianGetAll(
                            teknisiId = item.teknisiId,
                            email = item.email,
                            teknisiNama = item.teknisiNama,
                            teknisiNamaToko = item.teknisiNamaToko,
                            teknisiAlamat = item.teknisiAlamat,
                            teknisiLat = item.teknisiLat,
                            teknisiLng = item.teknisiLng,
                            teknisiHp = item.teknisiHp,
                            createdAt = item.createdAt,
                            updatedAt = item.updatedAt,
                            teknisiTotalScore = item.teknisiTotalScore,
                            teknisiTotalResponden = item.teknisiTotalResponden,
                            teknisiDeskripsi = item.teknisiDeskripsi,
                            teknisiFoto = item.teknisiFoto,
                            teknisiSertifikat = item.teknisiSertifikat
                        )
                        val directionTechnicianGetAll = HomeFragmentDirections.actionHomeFragmentToServiceDetailFragment(itemGetAll)
                        findNavController().navigate(directionTechnicianGetAll)
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
                    Timber.d(" vuvvvuu ${technicianGetAll.getData()}")
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
            binding.rvTerpopuler.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<TechnicianGetAll, ItemPopulerViewHolder>(R.layout.item_teknisi_terpopuler) {
                    onBind(::ItemPopulerViewHolder) { _, item ->
                        tvTeknisiName.text = item.teknisiNama
                        tvRating.text = String.format(
                            "%.1f",
                            (item.teknisiTotalScore / item.teknisiTotalResponden)
                        ).toDouble().toString()
                        Glide
                            .with(requireActivity())
                            .load(item.teknisiFoto)
                            .centerCrop()
//                            .placeholder(R.drawable.loading_spinner)
                            .into(ivTeknisi);
                    }

                    onClick {
                        val directionTechnicianGetAll = HomeFragmentDirections.actionHomeFragmentToServiceDetailFragment(item)
                        findNavController().navigate(directionTechnicianGetAll)
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