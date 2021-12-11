package com.example.feature_home.account

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.afollestad.vvalidator.form
import com.example.core_data.APP_SERTIFIKAT_IMAGES_URL
import com.example.core_data.APP_TEKNISI_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.TeknisiRequest
import com.example.core_data.api.request.UserRequest
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.Skils
import com.example.core_data.domain.auth.Auth
import com.example.core_resource.hideProgressDialog
import com.example.core_resource.showApiFailedDialog
import com.example.core_resource.showProgressDialog
import com.example.core_util.*
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentEditAccountTechnicianBinding
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class EditAccountTechnicianFragment : Fragment() {

    private var _binding: FragmentEditAccountTechnicianBinding? = null
    private val binding get() = _binding!!

    private val accountViewModel: AccountViewModel by viewModel()

    private var teknisiId: Int? = null
    private var userId: Int? = null
    private var authName: String? = null
    private var imagePath: String? = null
    private var sertifikatPath: String? = null

    private val textHintEmptyEmail by lazy {
        "Email harus diisi"
    }
    private val textHintEmptyHp by lazy {
        "Nomor Handphone harus diisi"
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentEditAccountTechnicianBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observer()
        setInput()
        observePhotoTeknisi()
        observeSertifikatTeknisi()
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setInput() {
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

            btnTeknisiSertifikat.setOnClickListener {
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

            form {
                useRealTimeValidation(disableSubmit = true)
                inputLayout(R.id.edt_layout_name){
                    isNotEmpty().description(textHintEmptyName)
                }
                inputLayout(R.id.edt_layout_email){
                    isNotEmpty().description(textHintEmptyEmail)
                }
                inputLayout(R.id.edt_layout_hp){
                    isNotEmpty().description(textHintEmptyHp)
                }
                inputLayout(R.id.edt_layout_store_name){
                    isNotEmpty().description(textHintEmptyStoreName)
                }
                inputLayout(R.id.edt_layout_store_address){
                    isNotEmpty().description(textHintEmptyStoreAddress)
                }
                inputLayout(R.id.edt_layout_store_desc){
                    isNotEmpty().description(textHintEmptyStoreDescription)
                }
                submitWith(R.id.btn_update) { updateEdit() }
            }
            btnUpdate.bindLifecycle(viewLifecycleOwner)
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
        if (galleryType.type == GalleryInputType.Profile.type) {
            resultPickProfile.launch(intent)
        } else {
            resultPickSertifikat.launch(intent)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val resultPickProfile = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val imageUri: Uri? = result.data?.data
        val filePathColumn = arrayOf(MediaStore.Images.Media._ID)
        binding.imageProfile.load(imageUri){
            crossfade(true)
            transformations(CircleCropTransformation())
        }

        if(imageUri != null && result.data != null){
            imagePath = convertImagePath(result?.data!!, imageUri, filePathColumn)
            accountViewModel.updatePhotoTeknisi(teknisiId!!, imagePath!!, imageUri, requireActivity().contentResolver, requireContext())
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private val resultPickSertifikat = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->
        val imageUri: Uri? = result.data?.data
        val filePathColumn = arrayOf(MediaStore.Images.Media._ID)
        binding.btnTeknisiSertifikat.load(imageUri){
            crossfade(true)
        }

        if(imageUri != null && result.data != null){
            sertifikatPath = convertImagePath(result?.data!!, imageUri, filePathColumn)
            accountViewModel.updateSertifikatTeknisi(teknisiId!!, sertifikatPath!!, imageUri, requireActivity().contentResolver, requireContext())
        }
    }

    private fun updatePhotoAuthLocally(filePath: String) {
        val newFoto = "${authName}_$filePath"
        accountViewModel.updateAuthPhotoLocally(userId as Int, newFoto)
    }

    private fun updateSertifikatAuthLocally(filePath: String) {
        val newFoto = "${authName}_$filePath"
        accountViewModel.updateAuthSertifikatLocally(userId as Int, newFoto)
    }

    private fun updateEdit() {
        dismissKeyboard()

        val userRequest = UserRequest(userId ?: 0, nama = "${binding.edtInputName.text}", email = "${binding.edtInputEmail.text}", noHp = "${binding.edtInputHp.text}")
        val teknisiRequest = TeknisiRequest(teknisiId ?: 0, namaToko = "${binding.edtInputStoreName.text}", teknisiAlamat = "${binding.edtInputStoreAddress.text}", deskripsi = "${binding.edtInputStoreDesc.text}")
        val jenisHpRequest = accountViewModel.getFinalJenisHpRequest()
        val jenisKerusakanHpRequest = accountViewModel.getFinalJenisKerusakanHpRequest()

        accountViewModel.updateTechinicial(teknisiId ?: 0, user = userRequest, teknisi = teknisiRequest, jenisHp = jenisHpRequest, jenisKerusakanHp = jenisKerusakanHpRequest )

        accountViewModel.isSaveForm.observe(viewLifecycleOwner){ event ->
            when (event) {
                is ApiEvent.OnProgress -> {
                    showProgressDialog()
                }
                is ApiEvent.OnSuccess -> if (event.hasBeenConsumed.not()) {
                    hideProgressDialog()
                    Toast.makeText(requireContext(), "Berhasil diupdate!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.accountFragment)
                }
                is ApiEvent.OnFailed -> if (event.hasBeenConsumed.not()) {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }

    private fun observer() {

        accountViewModel.authUser.observe(viewLifecycleOwner, { auth ->
            binding.edtInputEmail.isEnabled = false
            with(binding){
                auth?.let {

                    userId = auth.id
                    teknisiId = auth.teknisiId
                    authName = auth.name

                    if (auth.foto.isNotEmpty()) {
                        imageProfile.load(APP_TEKNISI_IMAGES_URL + auth.foto) {
                            crossfade(true)
                            transformations(CircleCropTransformation())
                        }
                    }
                    if (auth.teknisiSertifikat.isNotEmpty()) {
                        btnTeknisiSertifikat.load(APP_SERTIFIKAT_IMAGES_URL + auth.teknisiSertifikat) {
                            crossfade(true)
                        }
                    }

                    edtInputName.text = auth.name.toEditable()
                    edtInputEmail.text = auth.email.toEditable()
                    edtInputHp.text = auth.hp.toEditable()
                    edtInputHp.text = auth.hp.toEditable()
                    edtInputStoreAddress.text = auth.alamat.toEditable()
                    edtInputStoreName.text = auth.namaToko.toEditable()
                    edtInputHp.text = auth.hp.toEditable()
                    edtInputStoreDesc.text = auth.deskripsi.toEditable()
                    accountViewModel.setCurrentSkill(auth.teknisiId)
                }
            }
        })

        accountViewModel.liveSkils.observe(viewLifecycleOwner, { event ->
            when(event) {
                is ApiEvent.OnProgress -> {
                    Timber.d("Loading...")
                }
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    setupRecyclerSkils(it)
                }
                is ApiEvent.OnFailed -> if (!event.hasNotBeenConsumed) {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        })

    }

    private fun observePhotoTeknisi() {
        accountViewModel.photoTeknisiUpdate.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {
                    binding.btnChangePhoto.isEnabled = false
                }
                is ApiEvent.OnSuccess -> {
                    binding.btnChangePhoto.isEnabled = true
                    Snackbar.make(requireContext(), requireView(), "Foto profile berhasil diupdate!", Snackbar.LENGTH_SHORT).show()
                    updatePhotoAuthLocally(imagePath.toString())
                }
                is ApiEvent.OnFailed -> {
                    binding.btnChangePhoto.isEnabled = true
                    Snackbar.make(requireContext(), requireView(), "Foto profile gagal diupdate!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeSertifikatTeknisi() {
        accountViewModel.sertifikatTeknisiUpdate.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {
                    binding.btnTeknisiSertifikat.isEnabled = false
                }
                is ApiEvent.OnSuccess -> {
                    binding.btnTeknisiSertifikat.isEnabled = true
                    Snackbar.make(requireContext(), requireView(), "Sertifikat berhasil diupdate!", Snackbar.LENGTH_SHORT).show()
                    updateSertifikatAuthLocally(sertifikatPath.toString())
                }
                is ApiEvent.OnFailed -> {
                    binding.btnTeknisiSertifikat.isEnabled = true
                    Snackbar.make(requireContext(), requireView(), "Sertifikat gagal diupdate!", Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun setupRecyclerSkils(listSkils: ResultSkils) {
        accountViewModel jenisKerusakan listSkils.skils
        accountViewModel jenisHp listSkils.jenisHp
        binding.rvSkils.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.skils))
            withItem<Skils, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.isChecked = true
                    accountViewModel.putEditDataValue(
                        inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN,
                        itemId =item.id,
                        indexId = index,
                        default = true
                    )
                    titleCheckBox.text = item.namaKerusakan
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        accountViewModel.putEditDataValue(
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_KERUSAKAN,
                            itemId =item.id,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
        binding.rvHpType.setup {
            withLayoutManager(LinearLayoutManager(requireContext()))
            withDataSource(dataSourceTypedOf(listSkils.jenisHp))
            withItem<JenisHp, ItemViewHolder>(R.layout.layout_items){
                onBind(::ItemViewHolder){ index, item ->
                    titleCheckBox.isChecked = true
                    accountViewModel.putEditDataValue(
                        inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_HP,
                        itemId =item.id,
                        indexId = index,
                        default = true
                    )
                    titleCheckBox.text = item.jenisNama
                    titleCheckBox.setOnCheckedChangeListener { _, isChecked ->
                        accountViewModel.putEditDataValue(
                            inputType = TypeInput.ITEM_INPUT_TYPE_JENIS_HP,
                            itemId =item.id,
                            indexId = index,
                            value = if (isChecked) "1" else "0"
                        )
                    }
                }
            }
        }
    }

}

internal enum class TypeInput(val inputType: String){
    ITEM_INPUT_TYPE_JENIS_HP("JENIS_HP"),
    ITEM_INPUT_TYPE_JENIS_KERUSAKAN("JENIS_KERUSAKAN"),
}

internal enum class GalleryInputType(val type: String) {
    Profile("PROFILE"),
    Sertifikat("SERTIFIKAT")
}

fun Fragment.replaceFragmentEditAccountTechnician(resId: Int){
    val editAccountTechicianFragment = EditAccountTechnicianFragment()
    childFragmentManager.commit {
        replace(resId,editAccountTechicianFragment)
    }
}