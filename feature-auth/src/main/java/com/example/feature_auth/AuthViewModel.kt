package com.example.feature_auth

import androidx.lifecycle.*
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.auth.Auth
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    val auth = liveData<Auth?> {
        emit(authRepository.getAuth())
    }

    var email = ""
    var password = ""

    private val _loginRequest = MutableLiveData<ApiEvent<Auth?>>()
    val loginRequest: LiveData<ApiEvent<Auth?>> = _loginRequest

    private val _registerServiceResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val registerServiceResponse: LiveData<ApiEvent<CommonResponse?>> = _registerServiceResponse

    private val _registerServiceSuccess = MutableLiveData<Boolean>()
    val registerServiceSuccess: LiveData<Boolean> = _registerServiceSuccess

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authRepository.login(email, password)
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect { _loginRequest.value = it }
        }
    }

    fun registerService(
        email: String,
        teknisiNama: String,
        teknisiNoHp: String,
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
                teknisiNoHp = teknisiNoHp,
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

    fun registerPelanggan(
        email: String,
        teknisiNama: String,
        teknisiNoHp: String,
        password: String,
        teknisiAlamat: String,
        teknisiLat: Float,
        teknisiLng: Float,
    ) {
        viewModelScope.launch {
            authRepository.registerPelanggan(
                email = email,
                teknisiNama = teknisiNama,
                teknisiNoHp = teknisiNoHp,
                password = password,
                teknisiAlamat = teknisiAlamat,
                teknisiLat = teknisiLat,
                teknisiLng = teknisiLng,
            )
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _registerServiceSuccess.value = it is ApiEvent.OnSuccess<*>
                    _registerServiceResponse.value = it
                }
        }
    }
}