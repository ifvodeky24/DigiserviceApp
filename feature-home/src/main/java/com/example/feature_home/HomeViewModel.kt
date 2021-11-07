package com.example.feature_home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.technician.ListTechnicianGetAll
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_data.repository.TechnicianRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class HomeViewModel(
    private val technicianRepository: TechnicianRepository
) : ViewModel() {

    private val _technicianGetAllResponse = MutableLiveData<ApiEvent<ListTechnicianGetAll?>>()
    val technicianGetAllResponse: LiveData<ApiEvent<ListTechnicianGetAll?>> = _technicianGetAllResponse

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
}