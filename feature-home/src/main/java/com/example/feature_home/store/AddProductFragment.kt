package com.example.feature_home.store

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
import android.widget.RadioButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.ViewHolder
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.datasource.emptyDataSource
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.JenisHp
import com.example.core_resource.showApiFailedDialog
import com.example.core_util.*
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentAddProductBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddProductFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private var isFirstImageSelected = false

    private val imagePathList = arrayListOf<String>()
    private val imageUriList = arrayListOf<Uri>()

    private val productViewModel: ProductViewModel by viewModel()
    private val accountViewModel: AccountViewModel by viewModel()

    private var dataSource = emptyDataSource()

    private val textBtnAdd by lazy {
        "TAMBAH PRODUK"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        obserTypeHandphone()

        binding.firstImageView.setOnClickListener(this)
        binding.btnAddProduct.setOnClickListener(this)

    }

    private fun obserTypeHandphone() {

        productViewModel.jenisHp()
        productViewModel.liveJenisHp.observe(viewLifecycleOwner, { event ->
            when(event)
            {
                //is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    setupRecycler(it)
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        })

        productViewModel.uploadItemProdukResponse.observe(viewLifecycleOwner){ event ->
            when(event)
            {
                is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> event.getData()?.let {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.productFragment)
                    hideProgress()
                }
                is ApiEvent.OnFailed ->if (!event.hasNotBeenConsumed) {
                    hideProgress(true)
                }
            }
        }
    }

    private fun showProgress() = with(binding) {
        listOf(
            btnAddProduct, layoutProductName, layoutProductDescription, edtInputProductPrice
        ).forEach { it.isEnabled = false }

        btnAddProduct.showProgress()
    }

    private fun hideProgress(isEnable: Boolean = true) = with(binding) {
        btnAddProduct.postDelayed(
            {
                listOf(
                    btnAddProduct, layoutProductName, layoutProductDescription, edtInputProductPrice
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnAddProduct.hideProgress(textBtnAdd) {
            isEnable && with(binding) {
                "${edtInputProductName.text}".isNotBlank() && "${edtInputProductDescription.text}".isNotBlank() && "${edtInputProductPrice.text}".isNotBlank() && "${productViewModel.typeFilter}".isNotBlank()
            }
        }
    }

    private fun setupRecycler(listJenisHp: List<JenisHp>) {
        if (listJenisHp.isNotEmpty()){
            dataSource = dataSourceTypedOf(listJenisHp)
            binding.rvTypePhone.setup {
                withDataSource(dataSource)
                withItem<JenisHp, ItemTypeHpViewHolder>(R.layout.layout_items_radio_button){
                    onBind(::ItemTypeHpViewHolder){ _, item ->
                        titleRadioButton.text = item.jenisNama
                        if (productViewModel.filter == ""){
                            titleRadioButton.isChecked = item.jenisId == 1
                            productViewModel.typeFilter = 1
                            productViewModel.filter = "xiamoi"
                        }
                        else{
                            titleRadioButton.isChecked = item.jenisNama == productViewModel.filter
                        }
                        titleRadioButton.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked){
                                productViewModel.filter = item.jenisNama
                                productViewModel.typeFilter = item.jenisId

                                dataSource.set(listJenisHp)
                            }
                            else titleRadioButton.isChecked = false
                        }
                    }
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.apply {
            setNavigationIcon(R.drawable.ic_arrow_back)
            setNavigationOnClickListener {
                activity?.onBackPressed()
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.firstImageView -> showBottomSheet()
            R.id.btn_add_product -> {
                getImagePathList()
                imageUriList.forEachIndexed { index, _ ->
                    uploadData(index)
                }
            }
        }
    }

    private fun showBottomSheet(flag: String = Constants.ONE) {
        productViewModel.flagChoose = flag
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            navigateToGallery(flag)
        }
        else {
            if (isCameraPermissionGranted()){
                navigateToGallery(flag)
            }
            else{
                requestPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private val requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()){ permission ->
        if (permission){
            navigateToGallery(productViewModel.flagChoose)
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

    private fun navigateToGallery(flag: String){
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png"))
        }

        when(flag){
            Constants.ONE -> {
                resultFirstPhoto.launch(intent)
            }
        }
    }

    private val resultFirstPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        val selectedImage: Uri? = result.data?.data

        val filePathColumn = arrayOf(MediaStore.Images.Media._ID)

        binding.firstImageView.loadImage(selectedImage)

        if (selectedImage != null && result.data != null){
            productViewModel.firstImagePath = convertImagePath(result?.data!!, selectedImage, filePathColumn)
        }

        productViewModel.firstImageUri = selectedImage

        isFirstImageSelected = true

    }

    private fun getImagePathList() {
        if (productViewModel.firstImagePath.isNotEmpty()) {
            imagePathList.add(productViewModel.firstImagePath)
            productViewModel.firstImageUri?.let {
                imageUriList.add(it)
            }
        }
    }

    private fun uploadData(imageCount: Int) {
        if (isFirstImageSelected) {
            accountViewModel.authUser.observe(viewLifecycleOwner){
                productViewModel.setUploadItemImage(
                    filePath = imagePathList[imageCount],
                    uri = imageUriList[imageCount],
                    judul = binding.edtInputProductName.text.toString(),
                    deskripsi = binding.edtInputProductDescription.text.toString(),
                    harga = binding.edtInputProductPrice.text.toString(),
                    userId = it?.id.toString(),
                    jenisHpId = productViewModel.typeFilter.toString(),
                    contentResolver = requireActivity().contentResolver
                )
            }
            isFirstImageSelected = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}

class ItemTypeHpViewHolder(view: View) : ViewHolder(view) {
    val titleRadioButton: RadioButton = view.findViewById(R.id.radio_item)
}
