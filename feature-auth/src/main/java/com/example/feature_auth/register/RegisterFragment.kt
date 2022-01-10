package com.example.feature_auth.register

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.vvalidator.form
import com.example.core_data.api.ApiEvent
import com.example.core_util.*
import com.example.feature_auth.AuthViewModel
import com.example.feature_auth.R
import com.example.feature_auth.databinding.FragmentRegisterBinding
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class RegisterFragment : Fragment() {

    private val mapsZoom: Float by lazy {
        12.0f
    }

    var imageUriPhoto : Uri? = null
    var imageUriSertifikat : Uri? = null
    var imageUriIdentitas : Uri? = null
    var imageUriTempatUsaha : Uri? = null

    lateinit var filePathColumnPhoto: Array<String>
    lateinit var filePathColumnSertifikat: Array<String>
    lateinit var filePathColumnIdentitas: Array<String>
    lateinit var filePathColumnTempatUsaha: Array<String>

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

    private var imagePath: String? = null
    private var sertifikatPath: String? = null
    private var identitasPath: String? = null
    private var tempatUsahaPath: String? = null

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

    private val authViewModel: AuthViewModel by sharedViewModel()

    private val PERMISSION_ID = 42

    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        getLastLocation()

        initToolbar()
        setupInput()
        observeLogin()
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
        observeWhenSuccessLogin()
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()){
            if (isLocationEnabled()){
                mFusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()){ task ->
                    val location: Location? = task.result
                    val locationAddress = LocationAddress()
                    location?.let { location ->
                        authViewModel.lat = "${location.latitude}"
                        authViewModel.lng = "${location.longitude}"
                        locationAddress.getAddressFromLocation(location.latitude, location.longitude, requireActivity().applicationContext, GeoCodeHandler())
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

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            interval = 0
            fastestInterval = 0
            numUpdates = 1
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mFusedLocationClient?.requestLocationUpdates(
            mLocationRequest, mLocationCallback, Looper.myLooper()
        )
    }

    private fun initToolbar() {
        with(binding.registerToolbar.toolbar){
            title = "Register Service"
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener { requireActivity().onBackPressed() }
        }
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

    @SuppressLint("NewApi")
    private fun setupInput() {

        with(binding){

            btnChangePhoto.setOnClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    navigateToGallery(GalleryInputType.Profile)
                }
                else {
                    if (isCameraPermissionGranted()){
                        navigateToGallery(GalleryInputType.Profile)
                    }
                    else{
                        requestPermissionProfile.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

            cardFotoSertifikat.setOnClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    navigateToGallery(GalleryInputType.Sertifikat)
                }
                else {
                    if (isCameraPermissionGranted()){
                        navigateToGallery(GalleryInputType.Sertifikat)
                    }
                    else{
                        requestPermissionSertifikat.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

            cardFotoTempatUsaha.setOnClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    navigateToGallery(GalleryInputType.TempatUsaha)
                }
                else {
                    if (isCameraPermissionGranted()){
                        navigateToGallery(GalleryInputType.TempatUsaha)
                    }
                    else{
                        requestPermissionTempatUsaha.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

            cardFotoIdentitas.setOnClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                    navigateToGallery(GalleryInputType.Identitas)
                }
                else {
                    if (isCameraPermissionGranted()){
                        navigateToGallery(GalleryInputType.Identitas)
                    }
                    else{
                        requestPermissionIdentitas.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            }

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

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionProfile = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if (permission){
            navigateToGallery(GalleryInputType.Profile)
        }
        else{
            Toast.makeText(requireContext(), "No Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionSertifikat = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if (permission){
            navigateToGallery(GalleryInputType.Sertifikat)
        }
        else{
            Toast.makeText(requireContext(), "No Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionTempatUsaha = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if (permission){
            navigateToGallery(GalleryInputType.TempatUsaha)
        }
        else{
            Toast.makeText(requireContext(), "No Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val requestPermissionIdentitas = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if (permission){
            navigateToGallery(GalleryInputType.Identitas)
        }
        else{
            Toast.makeText(requireContext(), "No Permission Granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun navigateToGallery(galleryType: GalleryInputType) {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }

        when(galleryType.type){
            GalleryInputType.Profile.type -> resultPickProfile.launch(intent)
            GalleryInputType.Sertifikat.type -> resultPickSertifikat.launch(intent)
            GalleryInputType.TempatUsaha.type -> resultPickTempatUsaha.launch(intent)
            GalleryInputType.Identitas.type -> resultPickIdentitas.launch(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val resultPickProfile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        imageUriPhoto = result.data?.data
        filePathColumnPhoto = arrayOf(MediaStore.Images.Media._ID)
        if (imageUriPhoto != null) {
            binding.imageProfile.load(imageUriPhoto){
                crossfade(true)
                transformations(CircleCropTransformation())
            }
        }

        if(imageUriPhoto != null && result.data != null){
            imagePath = convertImagePath(result?.data!!, imageUriPhoto!!, filePathColumnPhoto)
            //accountViewModel.updatePhotoTeknisi(teknisiId!!, imagePath!!, imageUri, requireActivity().contentResolver, requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val resultPickSertifikat = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        imageUriSertifikat = result.data?.data
        filePathColumnSertifikat = arrayOf(MediaStore.Images.Media._ID)
        if (imageUriSertifikat != null) {
            binding.ivSertifikat.load(imageUriSertifikat){
                crossfade(true)
            }
        }

        if(imageUriSertifikat != null && result.data != null){
            sertifikatPath = convertImagePath(result?.data!!, imageUriSertifikat!!, filePathColumnSertifikat)
            // accountViewModel.updateSertifikatTeknisi(teknisiId!!, sertifikatPath!!, imageUri, requireActivity().contentResolver, requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val resultPickTempatUsaha = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        imageUriTempatUsaha = result.data?.data
        filePathColumnTempatUsaha = arrayOf(MediaStore.Images.Media._ID)
        if (imageUriTempatUsaha != null) {
            binding.ivTempatUsaha.load(imageUriTempatUsaha){
                crossfade(true)
            }
        }

        if(imageUriTempatUsaha != null && result.data != null){
            tempatUsahaPath = convertImagePath(result?.data!!, imageUriTempatUsaha!!, filePathColumnTempatUsaha)
            //accountViewModel.updateSertifikatTeknisi(teknisiId!!, sertifikatPath!!, imageUri, requireActivity().contentResolver, requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val resultPickIdentitas = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        imageUriIdentitas = result.data?.data
        filePathColumnIdentitas = arrayOf(MediaStore.Images.Media._ID)
        if (imageUriIdentitas != null) {
            binding.ivIdentitas.load(imageUriIdentitas){
                crossfade(true)
            }
        }

        if(imageUriIdentitas != null && result.data != null){
            identitasPath = convertImagePath(result?.data!!, imageUriIdentitas!!, filePathColumnIdentitas)
            //accountViewModel.updateSertifikatTeknisi(teknisiId!!, sertifikatPath!!, imageUri, requireActivity().contentResolver, requireContext())
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
                teknisiLat = if (authViewModel.lat == "") 0F else authViewModel.lat.toFloat(),
                teknisiLng = if (authViewModel.lng == "") 0F else authViewModel.lng.toFloat(),
                teknisiDeskripsi = edtInputStoreDescription.text.toString(),
                fotoUri = imageUriPhoto!!,
                fotoPath = imagePath!!,
                sertifikatUri = imageUriSertifikat!!,
                sertifikatPath = sertifikatPath!!,
                identitasUri = imageUriIdentitas!!,
                identitasPath = identitasPath!!,
                tempatUsahaUri = imageUriTempatUsaha!!,
                tempatUsahaPath = tempatUsahaPath!!,
                contentResolver = requireContext().contentResolver,
                context = requireContext()
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    private fun showProgress() = with(binding) {
        listOf(
            btnNext, edtLayoutEmail, edtLayoutNoHp, edtLayoutStoreName, edtLayoutName, edtLayoutPwd,
            edtLayoutStoreAddress, edtLayoutStoreDescription,
        ).forEach { it.isEnabled = false }

        btnNext.showProgress()
    }

    private fun hideProgress(isEnable: Boolean) = with(binding) {
        btnNext.postDelayed(
            {
                listOf(
                    btnNext, edtLayoutEmail, edtLayoutNoHp, edtLayoutStoreName, edtLayoutName, edtLayoutPwd,
                    edtLayoutStoreAddress, edtLayoutStoreDescription,
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnNext.hideProgress(textBtnNext) {
            isEnable && with(binding) {
                "${edtInputName.text}".isNotBlank() && "${edtInputEmail.text}".isNotBlank() && "${edtInputNoHp.text}".isNotBlank() && "${edtInputStoreName.text}".isNotBlank() && "${edtInputPwd.text}".isNotBlank()
                        && "${edtInputStoreDescription.text}".isNotBlank() && "${edtInputStoreAddress.text}".isNotBlank()
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

internal enum class GalleryInputType(val type: String){
    Profile("PROFILE"),
    Sertifikat("SERTIFIKAT"),
    TempatUsaha("TEMPAT_USAHA"),
    Identitas("IDENTITAS")
}
