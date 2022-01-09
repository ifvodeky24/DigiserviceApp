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
