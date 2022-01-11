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
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductGetAll
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_navigation.ModuleNavigator
import com.example.core_util.Constants
import com.example.core_util.PreferenceManager
import com.example.feature_home.databinding.FragmentHomeBinding
import com.example.feature_home.service.navigateToServiceCustomerFragment
import com.example.feature_home.store.ProductViewModel
import com.example.feature_home.viewHolder.ItemPopulerViewHolder
import com.example.feature_home.viewHolder.ItemProductViewHolder
import com.google.android.gms.location.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment(), ModuleNavigator {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModel()
    private val productViewModel: ProductViewModel by viewModel()

    private lateinit var preferenceManager: PreferenceManager

    private val permissionId = 42

    private var teknisiId = 0
    private var level = ""
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preferenceManager = PreferenceManager(requireActivity())

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        getLastLocation()

        homeViewModel.technicianGetAll()
        productViewModel.productGetAll()

        observeAuth()
        observeProductGetAll()
        observeTechnicianGetAll()
        /*observeNearbyTechnician()*/

//        binding.toolbar.setOnMenuItemClickListener { menuItem ->
//            if (menuItem.itemId == R.id.chat) {
//                navigateToChatActivity()
//            }
//            true
//        }

        with(binding) {
            cvMarketplace.setOnClickListener {
                navigateToProductActivity("")
            }

            cvChat.setOnClickListener {
                navigateToChatActivity()
            }

            cvService.setOnClickListener {
                navigateToServiceActivity()
            }
        }
    }


//    private fun checkPermissions(): Boolean {
//        if (ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_COARSE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED &&
//            ActivityCompat.checkSelfPermission(
//                requireContext(),
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ) == PackageManager.PERMISSION_GRANTED
//        ) {
//            return true
//        }
//        return false
//    }
//
//    private fun requestPermissions() {
//        ActivityCompat.requestPermissions(
//            requireActivity(),
//            arrayOf(
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION
//            ),
//            permissionId
//        )
//    }
//
//    private fun isLocationEnabled(): Boolean {
//        val locationManager: LocationManager =
//            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
//            LocationManager.NETWORK_PROVIDER
//        )
//    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<String>,
//        grantResults: IntArray
//    ) {
//        if (requestCode == permissionId) {
//            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
//                getLastLocation()
//            }
//        }
//    }

//    private val mLocationCallback = object : LocationCallback() {
//        override fun onLocationResult(locationResult: LocationResult) {
//            locationResult.lastLocation.apply {
//                homeViewModel.lat = "$latitude"
//                homeViewModel.lat = "$latitude"
//            }
//        }
//    }

//    @SuppressLint("MissingPermission")
//    private fun requestNewLocationData() {
//        val mLocationRequest = LocationRequest().apply {
//            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//            interval = 0
//            fastestInterval = 0
//            numUpdates = 1
//        }
//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        mFusedLocationClient.requestLocationUpdates(
//            mLocationRequest, mLocationCallback, Looper.myLooper()
//        )
//    }

//    @SuppressLint("MissingPermission")
//    private fun getLastLocation() {
//        if (checkPermissions()) {
//            if (isLocationEnabled()) {
//                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
//                    val location: Location? = task.result
//                    location?.let {
//                        homeViewModel.lat = "${it.latitude}"
//                        homeViewModel.lng = "${it.longitude}"
//                        homeViewModel.findNearbyTechnician(homeViewModel.lat, homeViewModel.lng)
//                    } ?: run {
//                        requestNewLocationData()
//                    }
//                }
//            } else {
//                Toast.makeText(requireContext(), "Turn on location", Toast.LENGTH_SHORT).show()
//                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
//                    startActivity(this)
//                }
//            }
//        } else {
//            requestPermissions()
//        }
//    }

    private fun observeAuth() {
        productViewModel.auth.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                if (data.isLogin) {
                    Timber.d("teknisi Idd ${data.teknisiId}")
                    data.let {
                        level = it.level
                        teknisiId = it.teknisiId
                    }
                }
            }
        }
    }

    private fun observeProductGetAll() {
        productViewModel.productGetAllResponse.observe(viewLifecycleOwner) { productAll ->
            when (productAll) {
                is ApiEvent.OnProgress -> {
                    binding.shimmerHome.showShimmer(true)
                }
                is ApiEvent.OnSuccess -> productAll.getData().let {
                    onDataProductAllLoaded(productAll.getData()!!)
                    Timber.d(" uiuiuiui ${productAll.getData()}")
                    binding.shimmerHome.hideShimmer()
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" booom ${productAll.getException()}")
                    binding.shimmerHome.hideShimmer()
                }
            }
        }
    }

    private fun onDataProductAllLoaded(data: List<ProductGetAll>) {
        val userId = preferenceManager.getString(Constants.ID)
        val filter = data.filter { it.jualUserId.toString() != userId }
        Timber.d("uuups $filter")
        Timber.d("uuups1 $userId")
        Timber.d("uuupsw ${data.map { it.jualUserId }}")
        if (filter.isNotEmpty()) {
            binding.rvTerbaru.setup {
                withDataSource(dataSourceTypedOf(filter))
                withItem<ProductGetAll, ItemProductViewHolder>(R.layout.item_produk_terbaru) {
                    onBind(::ItemProductViewHolder) { _, item ->
                        tvProductName.text = item.jualJudul
                        tvProductDesciption.text = item.jualDeskripsi
                        Glide.with(requireActivity())
                            .load(APP_PRODUCT_IMAGES_URL + item.pathPhoto)
                            .centerCrop()
                            .into(ivProductPhoto)
                    }

                    onClick {
                        navigateToProductActivity(item.jualId.toString(), status = "2")
                    }
                }
            }
        }

        getToken()
    }

    private fun observeTechnicianGetAll() {
        homeViewModel.technicianGetAllResponse.observe(viewLifecycleOwner, { technicianGetAll ->
            when (technicianGetAll) {
                is ApiEvent.OnProgress -> {
                    binding.shimmerHome.showShimmer(true)
                }
                is ApiEvent.OnSuccess -> technicianGetAll.getData()?.let {
                    Timber.d(" vuvvvuu ${technicianGetAll.getData()}")
                    onDataTechnicianGetAllLoaded(technicianGetAll.getData()!!)
                    binding.shimmerHome.hideShimmer()
                }
                is ApiEvent.OnFailed -> if (!technicianGetAll.hasNotBeenConsumed) {
                    Timber.d(" booom ${technicianGetAll.getException()}")
                    binding.shimmerHome.hideShimmer()
                }
            }
        })
    }

    private fun onDataTechnicianGetAllLoaded(data: List<TechnicianGetAll>) {
        Timber.d("level $level")
        val filter =
            if (level == "teknisi") data.filter { it.teknisiId.toString() != teknisiId.toString() } else data
        if (filter.isNotEmpty()) {
            binding.rvTerpopuler.setup {
                withDataSource(dataSourceTypedOf(filter))
                withItem<TechnicianGetAll, ItemPopulerViewHolder>(R.layout.item_teknisi_terpopuler) {
                    onBind(::ItemPopulerViewHolder) { _, item ->
                        tvTeknisiName.text = item.teknisiNama
                        tvRating.text = String.format(
                            "%.1f", (item.teknisiTotalScore / item.teknisiTotalResponden)
                        )

                        ratingBar.rating =
                            (item.teknisiTotalScore / item.teknisiTotalResponden).toFloat()

                        Timber.d("sdsssssss ${item.teknisiFoto}")

                        Glide
                            .with(requireActivity())
                            .load(APP_TEKNISI_IMAGES_URL + item.teknisiFoto)
                            .centerCrop()
                            .into(ivTeknisi)
                    }

                    onClick {
                        val directionTechnicianGetAll =
                            HomeFragmentDirections.actionHomeFragmentToServiceDetailFragment(item)
                        findNavController().navigate(directionTechnicianGetAll)
                    }
                }
            }
        }
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnSuccessListener { token: String? ->
            updateToken(
                token.toString()
            )
        }
    }

    private fun updateToken(token: String) {
        val database = FirebaseFirestore.getInstance()
        val documentPath = preferenceManager.getString(Constants.KEY_SENDER_ID)
//        if (documentPath != null) {
//        }
        val documentReference =
            database.collection(Constants.KEY_COLLECTION_USERS).document(documentPath.toString())
        documentReference.update(Constants.KEY_FCM_TOKEN, token)
            .addOnSuccessListener { unused: Void? -> Timber.d("Token updated successfuly") }
            .addOnFailureListener { e: Exception? -> Timber.d("Unable update token : ${e?.message}") }

        preferenceManager.putString(Constants.KEY_FCM_TOKEN, token)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}