package com.example.feature_home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductGetAll
import com.example.core_data.domain.technician.NearbyTechnician
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_navigation.ModuleNavigator
import com.example.core_resource.showApiFailedDialog
import com.example.feature_home.databinding.FragmentHomeBinding
import com.example.feature_home.store.ProductViewModel
import com.example.feature_home.viewHolder.ItemNearbyViewHolder
import com.example.feature_home.viewHolder.ItemPopulerViewHolder
import com.example.feature_home.viewHolder.ItemProductViewHolder
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private val productViewModel: ProductViewModel by viewModel()

    private val permissionId = 42

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

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

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()

        homeViewModel.technicianGetAll()
        productViewModel.productGetAll()

        observeProductGetAll()
        observeTechnicianGetAll()
        observeNearbyTechnician()

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.search -> {
                    true
                }
                else -> false
            }
        }
    }


    private fun checkPermissions(): Boolean{
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            permissionId
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                getLastLocation()
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.apply {
                homeViewModel.lat = "$latitude"
                homeViewModel.lat = "$latitude"
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()){
            if (isLocationEnabled()){
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()){ task ->
                    val location: Location? = task.result
                    location?.let {
                        homeViewModel.lat = "${it.latitude}"
                        homeViewModel.lng = "${it.longitude}"
                        homeViewModel.findNearbyTechnician(homeViewModel.lat, homeViewModel.lng)
                    } ?: run {
                        requestNewLocationData()
                    }
                }
            }
            else{
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    startActivity(this)
                }
            }
        }
        else{
            requestPermissions()
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
                    }
                    is ApiEvent.OnFailed -> if (!findNearbyTechnician.hasNotBeenConsumed) {
                        showApiFailedDialog(findNearbyTechnician.getException())
                    }
                }
            })
    }

    private fun onDataFindNearbyTechnicianLoaded(data: List<NearbyTechnician>) {
        if (data.isNotEmpty()) {
            binding.rvTerdekat.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<NearbyTechnician, ItemNearbyViewHolder>(R.layout.item_teknisi_terdekat) {
                    onBind(::ItemNearbyViewHolder) { _, item ->
                        tvTeknisiName.text = item.teknisiNama
                        Glide
                            .with(requireActivity())
                            .load(item.teknisiFoto)
                            .centerCrop()
                            .into(ivTeknisi)
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
                            "%.1f",(item.teknisiTotalScore/item.teknisiTotalResponden)
                        )

                        ratingBar.rating = (item.teknisiTotalScore/item.teknisiTotalResponden).toFloat()

                        Glide
                            .with(requireActivity())
                            .load(APP_TEKNISI_IMAGES_URL+item.teknisiFoto)
                            .centerCrop()
                            .into(ivTeknisi)
                    }

                    onClick {
                        val directionTechnicianGetAll = HomeFragmentDirections.actionHomeFragmentToServiceDetailFragment(item)
                        findNavController().navigate(directionTechnicianGetAll)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}