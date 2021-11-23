package com.example.feature_home.service

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.servicehp.ListServiceHandphoneTechnicianGetAll
import com.example.core_data.repository.ServiceHandphoneRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class ServiceHandphoneViewModel(
    private val serviceHandphoneRepository: ServiceHandphoneRepository
) : ViewModel() {

    private val _serviceHandphoneByTechnician = MutableLiveData<ApiEvent<ListServiceHandphoneTechnicianGetAll?>>()
    val serviceHandphoneByTechnician: LiveData<ApiEvent<ListServiceHandphoneTechnicianGetAll?>> = _serviceHandphoneByTechnician

    fun getServiceHandphoneByTechnicianId(technicianId: Int) {
        viewModelScope.launch {
            serviceHandphoneRepository.getServiceHeadphoneByTechnician(technicianId)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _serviceHandphoneByTechnician.value = it }
        }
    }
}