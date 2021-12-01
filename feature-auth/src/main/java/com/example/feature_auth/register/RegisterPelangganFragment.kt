package com.example.feature_auth.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_util.*
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentRegisterPelangganBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterPelangganFragment : Fragment() {

    private val mapsZoom: Float by lazy {
        12.0f
    }

    private val callback = OnMapReadyCallback { googleMap ->
        val lat = authViewModel.lat
        val lng = authViewModel.lng
        val location = if (lat.isNotEmpty() && lng.isNotEmpty()){
            LatLng(lat.toDouble(), lng.toDouble())
        }
        else{
            LatLng(-0.989818, 113.915863)
        }
        googleMap.addMarker(MarkerOptions().position(location).title(binding.edtInputStoreAddress.text.toString()))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, mapsZoom))
    }

    private val PERMISSION_ID = 42

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val textBtnNext by lazy {
        "LANJUTKAN DAFTAR"
    }
    private val textHintEmptyEmail by lazy {
        "Email harus diisi"
    }
    private val textHintEmptyNoHp by lazy {
        "Nomer handphone harus diisi"
    }
    private val textHintEmptyPwd by lazy {
        "Password harus diisi"
    }
    private val textHintEmptyName by lazy {
        "Nama harus diisi"
    }
    private val textHintEmptyStoreAddress by lazy {
        "Alamat toko harus diisi"
    }

    private var _binding: FragmentRegisterPelangganBinding? = null
    private val binding get() = _binding!!

    private val REQUEST_LOCATION_PERMISSION = 1

    private val authViewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterPelangganBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

        with(binding.registerToolbar.toolbar){
            title = "Register Pelanggan"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
        setupInput()
        observeLogin()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        observeWhenSuccessLogin()
    }

    private fun observeLogin() {
        authViewModel.registerServiceResponse.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    hideProgress(true)
                    authViewModel.email = binding.edtInputEmail.text.toString()
                    authViewModel.password = binding.edtInputPwd.text.toString()

                    authViewModel.login(binding.edtInputEmail.text.toString(), binding.edtInputPwd.text.toString())
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed)
                {
                    hideProgress(true)
                }
            }
        })
    }

    private fun setupInput() {
        with(binding){
            form {
                useRealTimeValidation(disableSubmit = true)

                inputLayout(R.id.edt_layout_store_address){
                    isNotEmpty().description(textHintEmptyStoreAddress)
                }
                inputLayout(R.id.edt_layout_name){
                    isNotEmpty().description(textHintEmptyName)
                }
                inputLayout(R.id.edt_layout_email){
                    isNotEmpty().description(textHintEmptyEmail)
                }
                inputLayout(R.id.edt_layout_no_hp){
                    isNotEmpty().description(textHintEmptyNoHp)
                }

                inputLayout(R.id.edt_layout_pwd){
                    isNotEmpty().description(textHintEmptyPwd)
                }
                submitWith(R.id.btn_daftar) { registerService() }
            }
            btnDaftar.bindLifecycle(viewLifecycleOwner)
        }
    }

    private fun observeWhenSuccessLogin() {
        authViewModel.loginRequest.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> {
                    findNavController().navigate(R.id.chooseFragment)
                }
                is ApiEvent.OnFailed -> {
                    Snackbar.make(requireContext(), requireView(), "Gagal menuju halaman Choose, mohon masuk ke halaman login", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun registerService() {
        dismissKeyboard()
        with(binding) {
            authViewModel.registerPelanggan(
                email = edtInputEmail.text.toString(),
                teknisiNama = edtInputName.text.toString(),
                teknisiNoHp = edtInputNoHp.text.toString(),
                password = edtInputPwd.text.toString(),
                teknisiAlamat = edtInputStoreAddress.text.toString(),
                teknisiLat = authViewModel.lat.toFloat(),
                teknisiLng = authViewModel.lng.toFloat(),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    private fun showProgress() = with(binding) {
        listOf(
            btnDaftar, edtLayoutEmail, edtLayoutName, edtLayoutPwd,
            edtLayoutStoreAddress,
        ).forEach { it.isEnabled = false }

        btnDaftar.showProgress()
    }

    private fun hideProgress(isEnable: Boolean) = with(binding) {
        btnDaftar.postDelayed(
            {
                listOf(
                    btnDaftar, edtLayoutEmail, edtLayoutName, edtLayoutPwd,
                    edtLayoutStoreAddress,
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnDaftar.hideProgress(textBtnNext) {
            isEnable && with(binding) {
                "${edtInputName.text}".isNotBlank() && "${edtInputEmail.text}".isNotBlank()
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
            PERMISSION_ID
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
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                getLastLocation()
            }
        }
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.apply {
                authViewModel.lat = "$latitude"
                authViewModel.lat = "$latitude"
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
                    val locationAddress = LocationAddress()
                    location?.let {
                        authViewModel.lat = "${it.latitude}"
                        authViewModel.lng = "${it.longitude}"

                        locationAddress.getAddressFromLocation(it.latitude, it.longitude, requireActivity().applicationContext, GeoCodeHandler())

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

    internal inner class GeoCodeHandler : Handler(Looper.getMainLooper()) {
        override fun handleMessage(message: Message) {
            val locationAddress: String = when (message.what) {
                1 -> {
                    val bundle = message.data
                    bundle.getString("address") ?: "Empty Address"
                }
                else -> null.toString()
            }
            authViewModel.address = locationAddress
            binding.edtInputStoreAddress.text = authViewModel.address.toEditable()
        }
    }

}