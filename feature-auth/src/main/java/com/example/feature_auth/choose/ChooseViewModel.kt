package com.example.feature_auth.choose

import androidx.lifecycle.*
import com.example.core_data.api.ApiEvent
import com.example.core_data.domain.ListJenisHp
import com.example.core_data.domain.ListJenisKerusakan
import com.example.core_data.repository.AuthRepository
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChooseViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _liveJenisHp = MutableLiveData<ApiEvent<ListJenisHp?>>()
    val liveJenisHp: LiveData<ApiEvent<ListJenisHp?>> = _liveJenisHp

    private val _liveJenisKerusakan = MutableLiveData<ApiEvent<ListJenisKerusakan?>>()
    val liveJenisKerusakan: LiveData<ApiEvent<ListJenisKerusakan?>> = _liveJenisKerusakan

    fun jenisHp() {
        viewModelScope.launch {
            authRepository.getJenisHpAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisHp.value = it
                }
        }
    }

    fun jenisKerusakan() {
        viewModelScope.launch {
            authRepository.getJenisKerusakanAll()
                .onStart { emit(ApiEvent.OnProgress()) }
                .collect {
                    _liveJenisKerusakan.value = it
                }
        }
    }
}