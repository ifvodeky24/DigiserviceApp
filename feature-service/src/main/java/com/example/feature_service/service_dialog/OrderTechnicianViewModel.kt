package com.example.feature_service.service_dialog

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.ListJenisHp
import com.example.core_data.domain.ListJenisKerusakan
import com.example.core_data.repository.AuthRepository
import com.example.core_data.repository.ServiceHandphoneRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class OrderTechnicianViewModel(
    private val authRepository: AuthRepository,
    private val serviceHandphoneRepository: ServiceHandphoneRepository
) : ViewModel() {

    private val _liveJenisHp = MutableLiveData<ApiEvent<ListJenisHp?>>()
    val liveJenisHp: LiveData<ApiEvent<ListJenisHp?>> = _liveJenisHp

    private val _liveJenisKerusakan = MutableLiveData<ApiEvent<ListJenisKerusakan?>>()
    val liveJenisKerusakan: LiveData<ApiEvent<ListJenisKerusakan?>> = _liveJenisKerusakan

    private val _isSuccess = MutableLiveData(false)
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val saveForm = MutableLiveData<ApiEvent<CommonResponse?>>()
    val isSaveForm: LiveData<ApiEvent<CommonResponse?>> = saveForm

    fun jenisHp() {
        viewModelScope.launch {
            authRepository.getJenisHpAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisHp.value = it
                }
        }
    }

    fun jenisKerusakan() {
        viewModelScope.launch {
            authRepository.getJenisKerusakanAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisKerusakan.value = it
                }
        }
    }

    fun insertServiceHandphone(
        teknisiId: Int,
        pelangganId: Int,
        jenisHp: String,
        jenisKerusakan: String,
        deskripsiKerusakan: String,
        byKurir: Int
    ) {
        viewModelScope.launch {
            serviceHandphoneRepository.insertServiceHandphone(
                teknisiId,
                pelangganId,
                jenisHp,
                jenisKerusakan,
                deskripsiKerusakan,
                byKurir
            )
                .onStart {
                    emit(ApiEvent.OnProgress())
                }
                .collect {
                    _isSuccess.value = it is ApiEvent.OnSuccess<*>
                    saveForm.value = it
                }
        }
    }

}
