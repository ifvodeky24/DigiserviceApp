package com.example.feature_home.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.servicehp.ListServiceHandphoneByCustomerGetAll
import com.example.core_data.domain.servicehp.ListServiceHandphoneByTechnicianGetAll
import com.example.core_data.domain.servicehp.ServiceHandphoneByTechnicianGetAll
import com.example.core_data.repository.ServiceHandphoneRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ServiceHandphoneViewModel(
    private val serviceHandphoneRepository: ServiceHandphoneRepository
) : ViewModel() {

    private val _serviceHandphoneByTechnician = MutableLiveData<ApiEvent<ListServiceHandphoneByTechnicianGetAll?>>()
    val serviceHandphoneByTechnician: LiveData<ApiEvent<ListServiceHandphoneByTechnicianGetAll?>> = _serviceHandphoneByTechnician

    private val _serviceHandphoneByCustomer = MutableLiveData<ApiEvent<ListServiceHandphoneByCustomerGetAll?>>()
    val serviceHandphoneByCustomer: LiveData<ApiEvent<ListServiceHandphoneByCustomerGetAll?>> = _serviceHandphoneByCustomer

    private val _serviceHandphoneById = MutableLiveData<ApiEvent<ServiceHandphoneByTechnicianGetAll?>>()
    val serviceHandphoneByById: LiveData<ApiEvent<ServiceHandphoneByTechnicianGetAll?>> = _serviceHandphoneById

    private val _isUpdateServiceHandphone = MutableLiveData<ApiEvent<CommonResponse?>>()
    val isUpdateServiceHandphone: LiveData<ApiEvent<CommonResponse?>> = _isUpdateServiceHandphone

    fun getServiceHandphoneByTechnicianId(technicianId: Int) {
        viewModelScope.launch {
            serviceHandphoneRepository.getServiceHeadphoneByTechnician(technicianId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _serviceHandphoneByTechnician.value = it }
        }
    }

    fun getServiceHandphoneByCustomerId(customerId: Int) {
        viewModelScope.launch {
            serviceHandphoneRepository.getServiceHeadphoneByCustomer(customerId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _serviceHandphoneByCustomer.value = it }
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