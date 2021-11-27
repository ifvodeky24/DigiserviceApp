package com.example.feature_home.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.afollestad.recyclical.datasource.dataSourceTypedOf
import com.afollestad.recyclical.setup
import com.afollestad.recyclical.withItem
import com.bumptech.glide.Glide
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductGetAll
import com.example.core_util.Constants
import com.example.feature_home.HomeViewModel
import com.example.feature_home.R
import com.example.feature_home.account.AccountViewModel
import com.example.feature_home.databinding.FragmentProductBinding
import com.example.feature_home.viewHolder.ItemProductViewHolder
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class ProductFragment : Fragment() {

    private var _binding: FragmentProductBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModel()
    private val accountViewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentProductBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeUser()

        observeProductByUserId()

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            if (menuItem.itemId == R.id.action_add_product) {
                findNavController().navigate(R.id.addProductFragment)
            }
            true
        }
    }

    private fun observeUser() {
        accountViewModel.authUser.observe(viewLifecycleOwner) { auth ->
            if (auth != null) {
                productViewModel.productGetById(auth.id)
            }
        }
    }

    private fun observeProductByUserId() {
        productViewModel.productGetAllResponse.observe(viewLifecycleOwner) { productAll ->
            when (productAll) {
                is ApiEvent.OnProgress -> {}
                is ApiEvent.OnSuccess -> productAll.getData().let {
                    onDataProductAllLoaded(productAll.getData()!!)
                    Timber.d(" uiuiuiui ${productAll.getData()}")
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" booom ${productAll.getException()}")
                }
            }
        }
    }

    private fun onDataProductAllLoaded(data: List<ProductGetAll>) {
        if (data.isNotEmpty()) {
            binding.rvProduct.setup {
                withDataSource(dataSourceTypedOf(data))
                withItem<ProductGetAll, ItemProductViewHolder>(R.layout.item_product) {
                    onBind(::ItemProductViewHolder) { _, item ->
                        tvProductName.text = item.jualJudul
                        tvProductDesciption.text = item.jualDeskripsi
                        Glide.with(requireActivity())
                            .load(Constants.APP_IMAGES_URL+item.pathPhoto)
                            .centerCrop()
                            .into(ivProductPhoto)
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
