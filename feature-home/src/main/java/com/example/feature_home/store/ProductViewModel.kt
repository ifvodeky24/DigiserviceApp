package com.example.feature_home.store

import android.content.ContentResolver
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.ListJenisHp
import com.example.core_data.domain.store.ListProductGetAll
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

    var firstImagePath = Constants.EMPTY_STRING

    var firstImageUri: Uri? = null

    var flagChoose = Constants.EMPTY_STRING

    var filter = ""
    var typeFilter = 0

    private val _productGetAllResponse = MutableLiveData<ApiEvent<ListProductGetAll?>>()
    val productGetAllResponse: LiveData<ApiEvent<ListProductGetAll?>> = _productGetAllResponse

    private val _uploadItemProdukResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val uploadItemProdukResponse: LiveData<ApiEvent<CommonResponse?>> = _uploadItemProdukResponse

    private val _liveJenisHp = MutableLiveData<ApiEvent<ListJenisHp?>>()
    val liveJenisHp: LiveData<ApiEvent<ListJenisHp?>> = _liveJenisHp

    fun jenisHp() {
        viewModelScope.launch {
            authRepository.getJenisHpAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisHp.value = it
                }
        }
    }

    fun productGetAll() {
        viewModelScope.launch {
            storeRepository.productGetAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _productGetAllResponse.value = it }
        }
    }

    fun setUploadItemImage(filePath: String, uri: Uri, judul: String, deskripsi: String, harga: String, userId: String, jenisHpId: String, contentResolver: ContentResolver){
        viewModelScope.launch {
            storeRepository.uploadProduk(filePath, uri, judul, deskripsi, harga, userId, jenisHpId, contentResolver)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _uploadItemProdukResponse.value = it }
        }
    }
}