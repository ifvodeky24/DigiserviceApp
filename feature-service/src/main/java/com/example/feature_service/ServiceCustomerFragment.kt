package com.example.feature_service

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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.technician.NearbyTechnician
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_resource.showApiFailedDialog
import com.example.feature_service.databinding.FragmentServiceCustomerBinding
import com.example.feature_service.viewHolder.ItemServiceTerViewHolder
import com.example.feature_service.viewHolder.ItemServiceViewHolder
import com.google.android.gms.location.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ServiceCustomerFragment : Fragment() {

    private var _binding: FragmentServiceCustomerBinding? = null
    private val binding: FragmentServiceCustomerBinding
        get() = _binding as FragmentServiceCustomerBinding

    private val serviceViewModel: ServiceViewModel by viewModel()
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val permissionId = 42
    private var teknisiId = 0
    private var level = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentServiceCustomerBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        serviceViewModel.technicianGetAll()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        getLastLocation()

        observeAuth()
        observeServiceGetAll()
        observeNearbyTechnician()

        arrayOf(binding.btnServicePopuler, binding.btnServiceTerdekat)
            .forEach { button ->
                button.setOnClickListener {
                    val directionServiceSeeAll = ServiceCustomerFragmentDirections.actionServiceCustomerFragmentToSeeAllServiceFragment()
                    findNavController().navigate(directionServiceSeeAll)
                }
            }
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            permissionId
        )
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                getLastLocation()
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.apply {
                serviceViewModel.lat = "$latitude"
                serviceViewModel.lat = "$latitude"
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
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    location?.let {
                        serviceViewModel.lat = "${it.latitude}"
                        serviceViewModel.lng = "${it.longitude}"
                        serviceViewModel.findNearbyTechnician(serviceViewModel.lat, serviceViewModel.lng)
                    } ?: run {
                        requestNewLocationData()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                    startActivity(this)
                }
            }
        } else {
            requestPermissions()
        }
    }

    private fun observeAuth() {
        serviceViewModel.auth.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                if (data.isLogin) {
                    data.let {
                        level = it.level
                        teknisiId = it.teknisiId
                    }
                }
            }
        }
    }

    private fun observeServiceGetAll() {
        serviceViewModel.technicianGetAllResponse.observe(viewLifecycleOwner, { technicianGetAll ->
            when (technicianGetAll) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> technicianGetAll.getData()?.let {
                    Timber.d(" vuvvvuu ${technicianGetAll.getData()}")
                    onDataServiceReceive(technicianGetAll.getData()!!)
//                    binding.shimmerHome.hideShimmer()
                }
                is ApiEvent.OnFailed -> if (!technicianGetAll.hasNotBeenConsumed) {
                    Timber.d(" booom ${technicianGetAll.getException()}")
//                    binding.shimmerHome.hideShimmer()
                }
            }
        })
    }

    private fun observeNearbyTechnician() {
        serviceViewModel.findNearbyTechnicianResponse.observe(
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

    private fun onDataServiceReceive(data: List<TechnicianGetAll>) {
        val filter =
            if (level == "teknisi") data.filter { it.teknisiId.toString() != teknisiId.toString() } else data
        if (filter.isNotEmpty()) {
            binding.rvService.setup {
                withDataSource(dataSourceTypedOf(filter))
                withItem<TechnicianGetAll, ItemServiceViewHolder>(R.layout.item_service) {
                    onBind(::ItemServiceViewHolder) { _, item ->
                        tvServiceName.text = item.teknisiNama
                        Glide
                            .with(requireActivity())
                            .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
                            .centerCrop()
                            .into(ivService)
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
                        val directionTechnicianGetAll =
                            ServiceCustomerFragmentDirections.actionServiceCustomerFragmentToServiceDetailFragment(itemGetAll)
                        findNavController().navigate(directionTechnicianGetAll)
                    }
                }
            }
        }
        binding.rvServicePopuler.setup {
            withDataSource(dataSourceTypedOf(data))
            withItem<TechnicianGetAll, ItemServiceTerViewHolder>(R.layout.item_service_terservice) {
                onBind(::ItemServiceTerViewHolder) { _, item ->
                    tvServiceName.text = item.teknisiNama
                    Glide
                        .with(requireActivity())
                        .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
                        .centerCrop()
                        .into(ivService)
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
                    val directionTechnicianGetAll =
                        ServiceCustomerFragmentDirections.actionServiceCustomerFragmentToServiceDetailFragment(itemGetAll)
                    findNavController().navigate(directionTechnicianGetAll)
                }
            }
        }
    }

    private fun onDataFindNearbyTechnicianLoaded(data: List<NearbyTechnician>) {
        val filter =
            if (level == "teknisi") data.filter { it.teknisiId.toString() != teknisiId.toString() } else data

        if (filter.isNotEmpty()) {
            binding.rvServiceTerdekat.setup {
                withDataSource(dataSourceTypedOf(filter))
                withItem<NearbyTechnician, ItemServiceTerViewHolder>(R.layout.item_service_terservice) {
                    onBind(::ItemServiceTerViewHolder) { _, item ->
                        tvServiceName.text = item.teknisiNama
                        Glide
                            .with(requireActivity())
                            .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
                            .centerCrop()
                            .into(ivService)
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
                        val directionTechnicianGetAll =
                            ServiceCustomerFragmentDirections.actionServiceCustomerFragmentToServiceDetailFragment(
                                itemGetAll
                            )
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