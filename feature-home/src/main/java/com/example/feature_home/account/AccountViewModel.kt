package com.example.feature_home.account

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.ListSkils
import com.example.core_data.domain.ResultSkils
import com.example.core_data.domain.auth.Auth
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent

class AccountViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email: String = ""

    private val _authUser = MutableLiveData<Auth?>()
    val authUser: LiveData<Auth?> = _authUser

    private val _technicial =MutableLiveData<TechnicianGetAll?>()
    val technicial: LiveData<TechnicianGetAll?> = _technicial

    private val _liveSkils = MutableLiveData<ApiEvent<ResultSkils?>>()
    val liveSkils: LiveData<ApiEvent<ResultSkils?>> = _liveSkils

    init {
        viewModelScope.launch {
            authRepository.getCurrentUserAsFlow()
                .map { it.getData() }
                .collect {
                    _authUser.value = it
                }
        }
    }

    fun setCurrentTechinicial(email: String){
        viewModelScope.launch {
            authRepository.getCurrentTechnicianAsFlow(email)
                .map { it.getData() }
                .collect {
                    _technicial.value = it
                }
        }
    }

    fun setCurrentSkill(teknisiId: Int){
        viewModelScope.launch {
            authRepository.getCurrentSkilAll(teknisiId)
                .collect {
                    _liveSkils.value = it
                }
        }
    }
}