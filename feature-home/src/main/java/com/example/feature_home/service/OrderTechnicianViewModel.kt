package com.example.feature_home.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.request.RequestAddServiceHandphone
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.repository.ServiceHandphoneRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class OrderTechnicianViewModel(
    private val serviceHandphoneRepository: ServiceHandphoneRepository
) : ViewModel() {

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val saveForm = MutableLiveData<ApiEvent<CommonResponse?>>()
    val isSaveForm: LiveData<ApiEvent<CommonResponse?>> = saveForm

    fun insertServiceHandphone(
        teknisiId: Int,
        pelangganId: Int,
        jenisHp: String,
        jenisKerusakan: String,
        byKurir: Int
    ) {
//        viewModelScope.launch {
//            serviceHandphoneRepository.insertServiceHandphone(
//                teknisiId,
//                pelangganId,
//                jenisHp,
//                jenisKerusakan,
//                byKurir
//            )
//                .onStart {
//                    emit(ApiEvent.OnProgress())
//                }
//                .collect {
//                    _isSuccess.value = it is ApiEvent.OnSuccess<*>
//                    saveForm.value = it
//                }
//        }
    }

}