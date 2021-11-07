package com.example.core_data.repository

import com.example.core_data.api.ApiEvent
import com.example.core_data.api.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.auth.toDomain
import com.example.core_data.api.response.toDomain
import com.example.core_data.api.service.AuthService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.ListJenisHp
import com.example.core_data.domain.ListJenisKerusakan
import com.example.core_data.domain.auth.Auth
import com.example.core_data.persistence.dao.AuthDao
import com.example.core_data.persistence.entity.auth.toEntity
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val authService: AuthService,
    private val dao: AuthDao,
    private val jsonParser: Moshi,
) {

    fun login(
        email: String,
        password: String
    ): Flow<ApiEvent<Auth>> = flow {
        runCatching {
            val apiId = AuthService.Login

            val apiResult = apiExecutor.callApi(apiId) {
                authService.login(email, password)
            }

            val apiEvent: ApiEvent<Auth> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()

                is ApiResult.OnSuccess -> with(apiResult.response.result) {
                    dao.replace(this.toDomain().toEntity())
                    ApiEvent.OnSuccess.fromServer(this.toDomain())
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<Auth>())
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
    ) : Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = AuthService.RegiterService

            val apiResult =apiExecutor.callApi(apiId) {
                authService.registerService(
                    teknisiNama = teknisiNama,
                    password = password,
                    email = email,
                    teknisiNamaToko = teknisiNamaToko,
                    teknisiAlamat = teknisiAlamat,
                    teknisiLat = teknisiLat,
                    teknisiLng = teknisiLng,
                    teknisiDeskripsi = teknisiDeskripsi
                )
            }

            val apiEvent:ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response){
                    when {
                        message.equals(ApiException.FailedResponse.MESSAGE_FAILED, true) -> {
                            ApiException.FailedResponse(message).let {
                                it.toFailedEvent()
                            }
                        }
                        else -> ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)

        }.onFailure {
            emit(it.toFailedEvent<CommonResponse>())
        }
    }

    fun getJenisHpAll() :Flow<ApiEvent<ListJenisHp>> = flow {
        runCatching {
            val apiId = AuthService.GetJenisHpAll
            val apiResult = apiExecutor.callApi(apiId) {
                authService.getJenisHpAll()
            }

            val apiEvent: ApiEvent<ListJenisHp> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        if (isEmpty())
                        {
                            ApiEvent.OnSuccess.fromServer(emptyList())
                        }
                        else
                        {
                            ApiEvent.OnSuccess.fromServer(this)
                        }
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ListJenisHp>())
        }
    }

    fun getJenisKerusakanAll() :Flow<ApiEvent<ListJenisKerusakan>> = flow {
        runCatching {
            val apiId = AuthService.GetJenisKerusakanAll
            val apiResult = apiExecutor.callApi(apiId) {
                authService.getJenisKerusakanAll()
            }

            val apiEvent: ApiEvent<ListJenisKerusakan> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        if (isEmpty())
                        {
                            ApiEvent.OnSuccess.fromServer(emptyList())
                        }
                        else
                        {
                            ApiEvent.OnSuccess.fromServer(this)
                        }
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ListJenisKerusakan>())
        }
    }

}