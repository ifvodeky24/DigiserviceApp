package com.example.feature_auth

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import androidx.lifecycle.*
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.domain.ResultSkils
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

    var lat = ""
    var lng = ""
    var address = ""

    var email = ""
    var password = ""

    private val _loginRequest = MutableLiveData<ApiEvent<Auth?>>()
    val loginRequest: LiveData<ApiEvent<Auth?>> = _loginRequest

    private val _registerServiceResponse = MutableLiveData<ApiEvent<CommonResponse?>>()
    val registerServiceResponse: LiveData<ApiEvent<CommonResponse?>> = _registerServiceResponse

    private val _registerServiceSuccess = MutableLiveData<Boolean>()
    val registerServiceSuccess: LiveData<Boolean> = _registerServiceSuccess

    private val _liveSkills = MutableLiveData<ApiEvent<ResultSkils?>>()
    val liveSkills: LiveData<ApiEvent<ResultSkils?>> = _liveSkills

    private val _sendMessageRequest = MutableLiveData<ApiEvent<String?>>()
    val sendMessageRequest: LiveData<ApiEvent<String?>> = _sendMessageRequest

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
        fotoUri: Uri,
        fotoPath: String,
        sertifikatUri: Uri,
        sertifikatPath: String,
        identitasUri: Uri,
        identitasPath: String,
        tempatUsahaUri: Uri,
        tempatUsahaPath: String,
        contentResolver: ContentResolver,
        context: Context
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
                teknisiDeskripsi = teknisiDeskripsi,
                fotoUri = fotoUri,
                fotoPath = fotoPath,
                identitasUri = identitasUri,
                identitasPath = identitasPath,
                sertifikatUri = sertifikatUri,
                sertifikatPath = sertifikatPath,
                tempatUsahaUri = tempatUsahaUri,
                tempatUsahaPath = tempatUsahaPath,
                contentResolver = contentResolver,
                context = context
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
        fotoUri: Uri,
        fotoPath: String,
        identitasUri: Uri,
        identitasPath: String,
        contentResolver: ContentResolver,
        context: Context
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
                fotoUri = fotoUri,
                fotoPath = fotoPath,
                identitasUri = identitasUri,
                identitasPath = identitasPath,
                contentResolver = contentResolver,
                context = context
            )
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _registerServiceSuccess.value = it is ApiEvent.OnSuccess<*>
                    _registerServiceResponse.value = it
                }
        }
    }

    fun setCurrentSkill(teknisiId: Int) {
        viewModelScope.launch {
            authRepository.getCurrentSkilAll(teknisiId)
                .collect {
                    _liveSkills.value = it
                }
        }
    }

    fun sendMessage(messageBody: String, set: Boolean) {
        var sets = set
        if (sets == true){
            viewModelScope.launch {
                authRepository.sendMessage(messageBody)
                    .onStart { emit(ApiEvent.OnProgress()) }
                    .collect { _sendMessageRequest.value = it }
            }
        }

        sets = false

    }
}