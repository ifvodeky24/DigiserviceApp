package com.example.feature_home.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.servicehp.ListServiceHandphoneTechnicianGetAll
import com.example.core_data.domain.servicehp.ServiceHandphoneTechnicianGetAll
import com.example.core_data.repository.ServiceHandphoneRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ServiceHandphoneViewModel(
    private val serviceHandphoneRepository: ServiceHandphoneRepository
) : ViewModel() {

    private val _serviceHandphoneByTechnician = MutableLiveData<ApiEvent<ListServiceHandphoneTechnicianGetAll?>>()
    val serviceHandphoneByTechnician: LiveData<ApiEvent<ListServiceHandphoneTechnicianGetAll?>> = _serviceHandphoneByTechnician

    private val _serviceHandphoneById = MutableLiveData<ApiEvent<ServiceHandphoneTechnicianGetAll?>>()
    val serviceHandphoneById: LiveData<ApiEvent<ServiceHandphoneTechnicianGetAll?>> = _serviceHandphoneById

    private val _isUpdateServiceHandphone = MutableLiveData<ApiEvent<CommonResponse?>>()
    val isUpdateServiceHandphone: LiveData<ApiEvent<CommonResponse?>> = _isUpdateServiceHandphone

    fun getServiceHandphoneByTechnicianId(technicianId: Int) {
        viewModelScope.launch {
            serviceHandphoneRepository.getServiceHeadphoneByTechnician(technicianId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _serviceHandphoneByTechnician.value = it }
        }
    }

    fun getServiceHandphoneById(serviceHandphoneId: Int) {
        viewModelScope.launch {
            serviceHandphoneRepository.getServiceHeadphoneById(serviceHandphoneId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _serviceHandphoneById.value = it }
        }
    }

    fun updateServiceHandphone(serviceHandphoneId: Int, statusService: String) {
        viewModelScope.launch {
            serviceHandphoneRepository.updateServiceHandphone(serviceHandphoneId, statusService)
                .onStart {
                    emit(ApiEvent.OnProgress())
                }
                .collect {
                    _isUpdateServiceHandphone.value = it
                }
        }
    }
}