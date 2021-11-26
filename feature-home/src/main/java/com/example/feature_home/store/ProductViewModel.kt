package com.example.feature_home.store

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.store.ListProductGetAll
import com.example.core_data.domain.store.ProductDetail
import com.example.core_data.repository.StoreRepository
import com.example.core_util.Constants
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ProductViewModel(
    private val storeRepository: StoreRepository
) : ViewModel() {

    var firstImagePath = Constants.EMPTY_STRING
    var secImagePath = Constants.EMPTY_STRING
    var thirdImagePath = Constants.EMPTY_STRING
    var fourthImagePath = Constants.EMPTY_STRING
    var fifthImagePath = Constants.EMPTY_STRING

    var firstImageUri: Uri? = null
    var secImageUri: Uri? = null
    var thirdImageUri: Uri? = null
    var fourthImageUri: Uri? = null
    var fifthImageUri: Uri? = null

    var flagChoose = Constants.EMPTY_STRING


    private val _productGetAllResponse = MutableLiveData<ApiEvent<ListProductGetAll?>>()
    val productGetAllResponse: LiveData<ApiEvent<ListProductGetAll?>> = _productGetAllResponse

    private val _productDetailResponse = MutableLiveData<ApiEvent<ProductDetail?>>()
    val productDetailResponse: LiveData<ApiEvent<ProductDetail?>> = _productDetailResponse

    fun productGetAll() {
        viewModelScope.launch {
            storeRepository.productGetAll()
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
}