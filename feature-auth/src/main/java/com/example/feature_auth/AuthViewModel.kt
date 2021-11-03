package com.example.feature_auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.auth.Auth
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    var email = ""
    var password = ""
    var level = ""

    private val _loginRequest = MutableLiveData<ApiEvent<Auth?>>()
    val loginRequest: LiveData<ApiEvent<Auth?>> = _loginRequest

    private val _registerServiceResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val registerServiceResponse: LiveData<ApiEvent<CommonResponse?>> = _registerServiceResponse

    private val _registerServiceSuccess = MutableLiveData<Boolean>()
    val registerServiceSuccess: LiveData<Boolean> = _registerServiceSuccess

    fun login() {
        viewModelScope.launch {
            authRepository.login(email, password , level)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _loginRequest.value = it }
        }
    }

    fun registerService(
        email: String,
        teknisiNama: String,
        password: String,
        teknisiNamaToko: String,
        teknisiAlamat: String,
        teknisiLat: Float,
        teknisiLng: Float,
        teknisiDeskripsi: String,
    ) {
        viewModelScope.launch {
            authRepository.registerService(
                email = email,
                teknisiNama = teknisiNama,
                password = password,
                teknisiNamaToko = teknisiNamaToko,
                teknisiAlamat = teknisiAlamat,
                teknisiLat = teknisiLat,
                teknisiLng = teknisiLng,
                teknisiDeskripsi = teknisiDeskripsi
            )
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _registerServiceSuccess.value = it is ApiEvent.OnSuccess<*>
                    _registerServiceResponse.value = it
                }
        }
    }
}