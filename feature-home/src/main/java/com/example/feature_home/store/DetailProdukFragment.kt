package com.example.feature_home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_resource.showApiFailedDialog
import com.example.core_resource.showProgressDialog
import com.example.feature_home.R
import com.example.feature_home.databinding.FragmentDetailProdukBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class DetailProdukFragment : Fragment(), View.OnClickListener {

    private var _binding: FragmentDetailProdukBinding? = null
    private val binding: FragmentDetailProdukBinding get() = _binding!!

    private val args: DetailProdukFragmentArgs by navArgs()

    private val productViewModel: ProductViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailProdukBinding.inflate(inflater, container, false)
        return _binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupDisplay()
        observer()

        binding.backImageView.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.apply {
            btnEdit.setOnClickListener(this@DetailProdukFragment)
            btnDelete.setOnClickListener(this@DetailProdukFragment)
        }
    }

    private fun observer() {
        productViewModel.deleteProductResponse.observe(viewLifecycleOwner) { event ->
            when(event) {
                is ApiEvent.OnProgress -> {
                    showProgressDialog()
                }
                is ApiEvent.OnSuccess -> {
                    showProgressDialog()
                    findNavController().navigate(R.id.productFragment)
                }
                is ApiEvent.OnFailed -> {
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }

    private fun setupDisplay() {
        with(binding){
            args.produk?.apply {
                tvProductTitle.text = jualJudul
                tvPrice.text = jualHarga.toString()
                tvDescription.text = jualDeskripsi
                tvPhoneType.text = jenisNama
                tvProductStatus.text = jualStatus
                Glide.with(this@DetailProdukFragment)
                    .load(APP_PRODUCT_IMAGES_URL+pathPhoto)
                    .into(ivCustomerPhoto)
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.btn_edit -> {
                val updateDirections = DetailProdukFragmentDirections.actionDetailProdukFragmentToAddProductFragment(args.produk, null)
                findNavController().navigate(updateDirections)
            }
            R.id.btn_delete -> {
                args.produk?.let { product ->
                    productViewModel.deleteProduct(product.jualId)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}