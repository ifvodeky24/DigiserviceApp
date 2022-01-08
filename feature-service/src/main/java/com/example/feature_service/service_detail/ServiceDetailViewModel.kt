package com.example.feature_service.service_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.auth.Auth
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ServiceDetailViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _authUser = MutableLiveData<Auth?>()
    val authUser: LiveData<Auth?> = _authUser

    private val _liveSkils = MutableLiveData<ApiEvent<ResultSkils?>>()
    val liveSkils: LiveData<ApiEvent<ResultSkils?>> = _liveSkils

//    init {
//        viewModelScope.launch {
//            authRepository.getCurrentUserAsFlow()
//                .map { it.getData() }
//                .collect {
//                    _authUser.value = it
//                }
//        }
//    }

    fun setCurrentSkill(teknisiId: Int){
        viewModelScope.launch {
            authRepository.getCurrentSkilAll(teknisiId)
                .collect {
                    _liveSkils.value = it
                }
        }
    }

    fun getJenisKerusakanHpAll(){
        viewModelScope.launch {
            authRepository.getJenisKerusakanHpAll()
                .collect {
                    _liveSkils.value = it
                }
        }
    }


}