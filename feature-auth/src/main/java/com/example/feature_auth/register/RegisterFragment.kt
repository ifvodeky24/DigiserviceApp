package com.example.feature_auth.register

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_util.bindLifecycle
import com.example.core_util.dismissKeyboard
import com.example.core_util.hideProgress
import com.example.core_util.showProgress
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentRegisterBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val callback = OnMapReadyCallback { googleMap ->
        val sydney = LatLng(-0.989818, 113.915863)
        googleMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

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
    private val textHintEmptyPassword by lazy {
        "Password harus diisi"
    }
    private val textHintEmptyStoreName by lazy {
        "Nama toko harus diisi"
    }
    private val textHintEmptyStoreAddress by lazy {
        "Alamat toko harus diisi"
    }
    private val textHintEmptyStoreDescription by lazy {
        "Deskripsi harus diisi"
    }

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var map: GoogleMap

    private val REQUEST_LOCATION_PERMISSION = 1

    private val authViewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding.registerToolbar.toolbar){
            title = "Register Service"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
        setupInput()
        observeLogin()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        //binding.mapView.getMapAsync(this)
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
                inputLayout(R.id.edt_layout_store_name){
                    isNotEmpty().description(textHintEmptyStoreName)
                }
                inputLayout(R.id.edt_layout_store_address){
                    isNotEmpty().description(textHintEmptyStoreAddress)
                }
                inputLayout(R.id.edt_layout_store_description){
                    isNotEmpty().description(textHintEmptyStoreDescription)
                }
                submitWith(R.id.btn_next) { registerService() }
            }
            btnNext.bindLifecycle(viewLifecycleOwner)
                mapViewButton.setOnClickListener {
            }
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
            authViewModel.registerService(
                email = edtInputEmail.text.toString(),
                teknisiNama = edtInputName.text.toString(),
                teknisiNoHp = edtInputNoHp.text.toString(),
                password = edtInputPwd.text.toString(),
                teknisiNamaToko = edtInputStoreName.text.toString(),
                teknisiAlamat = edtInputStoreAddress.text.toString(),
                teknisiLat = 0f,
                teknisiLng = 0f,
                teknisiDeskripsi = edtInputStoreDescription.text.toString(),
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }


    private fun showProgress() = with(binding) {
        listOf(
            btnNext, edtLayoutEmail, edtLayoutName, edtLayoutPwd,
            edtLayoutStoreAddress, edtLayoutStoreDescription,
        ).forEach { it.isEnabled = false }

        btnNext.showProgress()
    }

    private fun hideProgress(isEnable: Boolean) = with(binding) {
        btnNext.postDelayed(
            {
                listOf(
                    btnNext, edtLayoutEmail, edtLayoutName, edtLayoutPwd,
                    edtLayoutStoreAddress, edtLayoutStoreDescription,
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnNext.hideProgress(textBtnNext) {
            isEnable && with(binding) {
                "${edtInputName.text}".isNotBlank() && "${edtInputEmail.text}".isNotBlank()
            }
        }
    }

//    override fun onMapReady(map: GoogleMap) {
//        val lat = -0.989818
//        val lng = 113.915863
//        val zoomLevel = 15f
//        val overlaySize = 100f
//
//        val defaultLatLng = LatLng(lat, lng)
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLatLng, zoomLevel))
//        map.addMarker(MarkerOptions().position(defaultLatLng))
//
//        val googleOverlay = GroundOverlayOptions()
//            .image(BitmapDescriptorFactory.fromResource(R.drawable.ic_map))
//            .position(defaultLatLng, overlaySize)
//        map.addGroundOverlay(googleOverlay)
//
//        //enableMyLocation()
//
//        if (isPermissionGranted()){
//
//        }
//        else{
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        }
//
//    }

//    private fun enableMyLocation() {
//        return ContextCompat.checkSelfPermission(
//            requireActivity(),
//            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        //map.isMyLocationEnabled = true

//        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) !=
//            PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) !=
//            PackageManager.PERMISSION_GRANTED) {
//                map.isMyLocationEnabled = true
//        }
//        else{
//            ActivityCompat.requestPermissions(
//                requireActivity(),
//                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
//                REQUEST_LOCATION_PERMISSION
//            )
//        }
//    }

    private fun isPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

//    private fun isPermissionGranted(): Boolean {
//        return ( ActivityCompat.checkSelfPermission(
//            requireActivity(),
//            Manifest.permission.ACCESS_FINE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//            requireActivity(),
//            Manifest.permission.ACCESS_COARSE_LOCATION
//        ) != PackageManager.PERMISSION_GRANTED)
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray) {
        // Check if location permissions are granted and if so enable the
        // location data layer.
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.contains(PackageManager.PERMISSION_GRANTED)) {
                //enableMyLocation()
            }
        }
    }

}