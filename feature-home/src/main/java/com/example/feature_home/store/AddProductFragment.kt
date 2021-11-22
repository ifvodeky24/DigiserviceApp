package com.example.feature_home.store

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import androidx.core.content.ContextCompat
import com.example.core_util.Constants
import com.example.core_util.convertImagePath
import com.example.core_util.loadImage
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentAddProductBinding
import okio.FileNotFoundException
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.ArrayList

class AddProductFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    private var isFirstImageSelected = false
    private var isSecImageSelected = false
    private var isThirdImageSelected = false
    private var isFourthImageSelected = false
    private var isFifthImageSelected = false

    private val imagePathList = arrayListOf<String>()
    private val imageUriList = arrayListOf<Uri>()

    val productViewModel: ProductViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
        setupToolbar()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.firstImageView.setOnClickListener(this)

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
        if (v?.id == R.id.firstImageView) {
            showBottomSheet(Constants.ONE)
        }
    }

    private fun showBottomSheet(flag: String) {
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

    fun navigateToGallery(flag: String){
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }

        when(flag){
            Constants.ONE -> {
                resultFirstPhoto.launch(intent)
            }
        }
    }

    private val resultFirstPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->

        val selectedImage: Uri? = result.data?.data

        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

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

    private fun callImageUpload(imageCount: Int) {
        if (isSecImageSelected) {
//            productViewModel.setUploadItemImageObj(
//                imagePathList.get(imageCount),
//                imageUriList.get(imageCount),
//                itemViewModel.itemId,
//                secImageId,
//                activity!!.contentResolver
//            )
            isSecImageSelected = false
        }
    }

    private fun setPhotoProduct(photo: Uri) {
        try {
            val imageStream = activity?.contentResolver?.openInputStream(photo)
            val selectedPhoto = BitmapFactory.decodeStream(imageStream)
            //binding.ivProductPhoto.setImageBitmap(selectedPhoto)
        } catch (err: FileNotFoundException) {
            err.printStackTrace()
            Toast.makeText(context, "Foto gagal di pilih!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}