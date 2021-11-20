package com.example.feature_home.store

import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.feature_home.HomeActivity
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentAddProductBinding
import com.example.feature_home.databinding.FragmentProductBinding
import okio.FileNotFoundException

class AddProductFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

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

        binding.buttonAddPhoto.setOnClickListener(this)
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
        if (v?.id == R.id.buttonAddPhoto) {
            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            resultPhoto.launch(intent)
        }
    }

    private val resultPhoto = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val photo = result.data?.data as Uri
            setPhotoProduct(photo)
        }
    }

    private fun setPhotoProduct(photo: Uri) {
        try {
            val imageStream = activity?.contentResolver?.openInputStream(photo)
            val selectedPhoto = BitmapFactory.decodeStream(imageStream)
            binding.ivProductPhoto.setImageBitmap(selectedPhoto)
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