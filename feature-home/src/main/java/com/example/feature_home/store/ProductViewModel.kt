package com.example.feature_home.store

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.bumptech.glide.load.model.FileLoader
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.ListJenisHp
import com.example.core_data.domain.auth.Auth
import com.example.core_data.domain.store.ListProductBuyHistoryGetAll
import com.example.core_data.domain.store.ListProductGetAll
import com.example.core_data.domain.store.ProductDetail
import com.example.core_data.repository.AuthRepository
import com.example.core_data.repository.StoreRepository
import com.example.core_util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(
    private val storeRepository: StoreRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val auth = liveData<Auth?> {
        emit(authRepository.getAuth())
    }

    var firstImagePath = Constants.EMPTY_STRING

    var firstImageUri: Uri? = null

    var flagChoose = Constants.EMPTY_STRING

    var filter = ""
    var typeFilter = 0

    internal var isUpdate = false

    internal var userId: Int = 0

    private val _productGetAllResponse = MutableLiveData<ApiEvent<ListProductGetAll?>>()
    val productGetAllResponse: LiveData<ApiEvent<ListProductGetAll?>> = _productGetAllResponse

    private val _uploadItemProdukResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val uploadItemProdukResponse: LiveData<ApiEvent<CommonResponse?>> = _uploadItemProdukResponse

    private val _deleteProductResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val deleteProductResponse: LiveData<ApiEvent<CommonResponse?>> = _deleteProductResponse

    private val _buyProductResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val buyProductResponse: LiveData<ApiEvent<CommonResponse?>> = _buyProductResponse

    private val _liveJenisHp = MutableLiveData<ApiEvent<ListJenisHp?>>()
    val liveJenisHp: LiveData<ApiEvent<ListJenisHp?>> = _liveJenisHp

    private val _historyBuyProduct = MutableLiveData<ApiEvent<ListProductBuyHistoryGetAll?>>()
    val historyBuyProduct: LiveData<ApiEvent<ListProductBuyHistoryGetAll?>> = _historyBuyProduct

    private val _updateStatusBeliProduct = MutableLiveData<ApiEvent<CommonResponse?>>()
    val updateStatusBeliProduct: LiveData<ApiEvent<CommonResponse?>> = _updateStatusBeliProduct

    private val _reviewProductResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val reviewProductResponse: LiveData<ApiEvent<CommonResponse?>> = _reviewProductResponse

    fun jenisHp() {
        viewModelScope.launch {
            authRepository.getJenisHpAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisHp.value = it
                }
        }
    }

    private val _productDetailResponse = MutableLiveData<ApiEvent<ProductDetail?>>()
    val productDetailResponse: LiveData<ApiEvent<ProductDetail?>> = _productDetailResponse

    fun productGetAll() {
        viewModelScope.launch {
            storeRepository.productGetAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _productGetAllResponse.value = it }
        }
    }

    fun productGetById(userId: Int) {
        viewModelScope.launch {
            storeRepository.productByUserId(userId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _productGetAllResponse.value = it }
        }
    }

    fun productDetail(id: Int) {
        viewModelScope.launch {
            storeRepository.productDetail(id)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _productDetailResponse.value = it }
        }
    }


        fun setUploadItemImage(filePath: String, uri: Uri, judul: String, deskripsi: String, harga: String, userId: String, jenisHpId: String, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            storeRepository.uploadProduk(filePath, uri, judul, deskripsi, harga, userId, jenisHpId, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _uploadItemProdukResponse.value = it }
        }
    }

    fun setUpdateProdukImage(id: Int, filePath: String, uri: Uri, judul: String, deskripsi: String, harga: String, userId: String, jenisHpId: String, contentResolver: ContentResolver, context: Context){
        viewModelScope.launch {
            storeRepository.updateImageProduk(id, filePath, uri, judul, deskripsi, harga, userId, jenisHpId, contentResolver, context)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _uploadItemProdukResponse.value = it }
        }
    }

    fun setUpdateItem(idJual: Int, judul: String, deskripsi: String, harga: Int, userId: Int, jenisHpId: Int){
        viewModelScope.launch {
            storeRepository.updateProduk(idJual, judul, deskripsi, harga, userId, jenisHpId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _uploadItemProdukResponse.value = it }
        }
    }

    internal fun deleteProduct(id: Int){
        viewModelScope.launch {
            storeRepository.deleteProduk(id)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _deleteProductResponse.value = it }
        }
    }

    fun buyProduct(beliJualId: Int, beliJasaKurir: String, beliPembeli:Int){
        viewModelScope.launch {
            storeRepository.buyProduct(beliJualId, beliJasaKurir, beliPembeli)
                .onStart { emit(ApiEvent.OnProgress())}
                .collect { _buyProductResponse.value = it }
        }
    }

    fun historyBuyProductGetById(userId: Int) {
        viewModelScope.launch {
            storeRepository.buyProductHistoryByUserId(userId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _historyBuyProduct.value = it }
        }
    }

    fun updateStatusBeliProduct(beliId: Int, beliStatus: String) {
        viewModelScope.launch {
            storeRepository.updateStatusBeliProduct(beliId, beliStatus)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _updateStatusBeliProduct.value = it }
        }
    }

    fun reviewProduct(beliId: Int, rating: Float, ratingDesc: String){
        viewModelScope.launch {
            storeRepository.reviewProduct(beliId = beliId, rating = rating, desc = ratingDesc)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _reviewProductResponse.value = it }
        }
    }
}