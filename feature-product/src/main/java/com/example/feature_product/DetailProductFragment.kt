package com.example.feature_product

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.core_data.APP_PRODUCT_IMAGES_URL
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ProductDetail
import com.example.core_navigation.ModuleNavigator
import com.example.core_resource.showApiFailedDialog
import com.example.core_util.*
import com.example.core_util.Constants.KEY_RECEIVER_ID
import com.example.core_util.Constants.KEY_RECEIVER_NAME
import com.example.core_util.Constants.KEY_RECEIVER_PHOTO
import com.example.feature_home.store.ProductViewModel
import com.example.feature_product.databinding.FragmentDetailProductBinding
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class DetailProductFragment : Fragment(), ModuleNavigator, View.OnClickListener {
    private var jualId: Int? = 0
    private val status by lazy { (activity as ProductActivity).status }

    private var _binding: FragmentDetailProductBinding? = null
    private val binding get() = _binding!!

    private val productViewModel: ProductViewModel by viewModel()

    private var beliPembeli: Int? = null

    private var byKurir: String = ""

    private lateinit var preferenceManager: PreferenceManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        jualId = arguments?.getString(MarketplaceFragment.JUAL_ID)?.toInt()

        preferenceManager = PreferenceManager(requireActivity())

        observeAuth()
        setupDisplay(jualId)
        setupInput()
        observeProductDetail()
        observeBuyProduct()

        childFragmentManager.setFragmentResultListener(
            BuyDialogFragment.KEY_RESULT_SUBMIT,
            this@DetailProductFragment
        ) { _, bundle ->
            val result = bundle.getString(BuyDialogFragment.KEY_BUNDLE_SUBMIT)
            if (result == BuyDialogFragment.TRUE) {
                buyProduct()
            }
        }
    }

    private fun observeAuth() {
        productViewModel.auth.observe(viewLifecycleOwner) { auth ->
            auth?.apply {
                beliPembeli = auth.id
            }
        }
    }

    private fun setupDisplay(jualIds: Int?) {
        if (status == "2") {
            jualId?.let { productViewModel.productDetail(it) }
        } else {
            if (jualIds != null) {
                productViewModel.productDetail(jualIds.toInt())
            }
        }
    }

    private fun setupInput() {
        with(binding) {
            btnOrder.setOnClickListener(this@DetailProductFragment)
        }

        if (status == "2") {
            binding.backImageView.setOnClickListener {
                navigateToHomeActivity(true)
            }

            requireActivity().onBackPressedDispatcher.addCallback(
                requireActivity(),
                object : OnBackPressedCallback(true) {
                    override fun handleOnBackPressed() {
                        navigateToHomeActivity(true)
                    }
                }
            )
        }
    }

    private fun observeBuyProduct() {
        productViewModel.buyProductResponse.observe(viewLifecycleOwner) { event ->
            when (event) {
                is ApiEvent.OnProgress -> showProgress()
                is ApiEvent.OnSuccess -> {
                    hideProgress(true)
                    navigateToHomeActivity(false)
                }
                is ApiEvent.OnFailed -> {
                    hideProgress(true)
                    val exception = event.getException()
                    showApiFailedDialog(exception)
                }
            }
        }
    }

    private fun showProgress() = with(binding) {
        listOf(
            btnByKurir, btnOrder
        ).forEach { it.isEnabled = false }

        btnOrder.showProgress()
    }

    private fun hideProgress(isEnable: Boolean = false) = with(binding) {
        btnOrder.postDelayed(
            {
                listOf(
                    btnByKurir, btnOrder
                ).forEach { it.isEnabled = true }
            }, 1000L
        )

        btnOrder.hideProgress("Beli") {
            isEnable && "$beliPembeli".isNotBlank() && byKurir.isNotBlank()
        }
    }


    private fun observeProductDetail() {
        productViewModel.productDetailResponse.observe(viewLifecycleOwner, { productDetail ->
            when (productDetail) {
                is ApiEvent.OnProgress -> {
                }
                is ApiEvent.OnSuccess -> productDetail.getData().let {
                    Timber.d(" productDetail dfdf ${productDetail.getData()}")
                    onDataProductDetailLoaded(productDetail.getData()!!)
                }
                is ApiEvent.OnFailed -> {
                    Timber.d(" productDetail booom ${productDetail.getException()}")
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun onDataProductDetailLoaded(data: ProductDetail) {

//        beliPembeli = data.jualUserId

        with(binding) {
            tvProductStatus.text = data.jualStatus
            tvName.text = data.jualJudul
            tvDate.text = data.jualTglPenjualan
            tvPrice.text = getFormatRupiah(data.jualHarga)
            tvTypeName.text = data.jenisNama
            tvDescription.text = data.jualDeskripsi
            Glide.with(requireActivity())
                .load(APP_PRODUCT_IMAGES_URL + data.fotoProduk)
                .fitCenter()
                .into(ivProduct)
        }

        binding.chatButton.setOnClickListener {
            val database = FirebaseFirestore.getInstance()
            database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo("id", data.jualUserId)
                .get()
                .addOnCompleteListener { task: Task<QuerySnapshot?> ->
                    if (task.isSuccessful && task.result != null && task.result!!.documents.size > 0) {
                        val documentSnapshot = task.result!!.documents[0]
                        preferenceManager.putString(KEY_RECEIVER_ID, documentSnapshot.id)
                        preferenceManager.putString(
                            KEY_RECEIVER_NAME,
                            documentSnapshot.getString("name")
                        )
                        preferenceManager.putString(
                            KEY_RECEIVER_PHOTO,
                            documentSnapshot.getString("foto")
                        )

                        navigateToChatActivity(
                            finnishCurrent = true,
                            status = "3",
                            productName = data.jenisNama,
                            productImage = data.fotoProduk
                        )
                    } else {
                        Timber.d("gagal")
                        Toast.makeText(
                            requireContext(),
                            "Pengguna ini tidak dapat melakukan chat",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_order -> {
                val bs = BuyDialogFragment()
                bs.show(childFragmentManager, "BuyBottomSheet")
            }
        }
    }

    private fun buyProduct() {
        byKurir = if (binding.kurirYes.isChecked) "Ya" else "Tidak"
        productViewModel.buyProduct(jualId as Int, byKurir, beliPembeli ?: 0)
    }
}