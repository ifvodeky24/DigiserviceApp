package com.example.core_data.repository

import com.example.core_data.api.ApiEvent
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.request.LoginRequest
import com.example.core_data.api.response.auth.toDomain
import com.example.core_data.api.service.AuthService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.auth.Auth
import com.example.core_data.persistence.dao.AuthDao
import com.example.core_data.persistence.entity.toDomain
import com.example.core_data.persistence.entity.toEntity
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
        password: String,
        level: String
    ): Flow<ApiEvent<Auth>> = flow {
        runCatching {
            val apiId = AuthService.Login
            val auth = requireNotNull(dao.selectAuth()?.toDomain())

            val body = LoginRequest(
                email = email,
                password = password,
                level = level
            )

            val apiResult = apiExecutor.callApi(apiId) {
                authService.login(body)
            }

            val apiEvent: ApiEvent<Auth> = when (apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> {
                    apiResult.response.result.toDomain(auth).run {
                        dao.replace(
                            auth.copy(
                                id = id,
                                name = name,
                                email = email,
                                password = password,
                                aksesId = aksesId,
                                level = level,
                                teknisiId = teknisiId,
                                isLogin = isLogin
                            ).toEntity()
                        )
                        ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }
            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<Auth>())
        }
    }
}