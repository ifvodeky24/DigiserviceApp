package com.example.feature_home

import androidx.lifecycle.*
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.auth.Auth
import com.example.core_data.domain.technician.ListNearbyTechnician
import com.example.core_data.domain.technician.ListTechnicianGetAll
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_data.repository.TechnicianRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel(
    private val technicianRepository: TechnicianRepository,
) : ViewModel() {

    private val _technicianGetAllResponse = MutableLiveData<ApiEvent<ListTechnicianGetAll?>>()
    val technicianGetAllResponse: LiveData<ApiEvent<ListTechnicianGetAll?>> = _technicianGetAllResponse

    private val _findNearbyTechnicianResponse = MutableLiveData<ApiEvent<ListNearbyTechnician?>>()
    val findNearbyTechnicianResponse: LiveData<ApiEvent<ListNearbyTechnician?>> = _findNearbyTechnicianResponse

//    init {
//        technicianGetAll()
//    }

    fun technicianGetAll() {
        viewModelScope.launch {
            technicianRepository.technicianGetAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _technicianGetAllResponse.value = it }
        }
    }

    fun findNearbyTechnician(latitude: String, longitude: String) {
        viewModelScope.launch {
            technicianRepository.findNearbyTechnician(latitude, longitude)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _findNearbyTechnicianResponse.value = it }
        }
    }
}