package com.example.core_data.repository

import com.example.core_data.api.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.request.ServiceHandphoneRequest
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.service.ServiceHandphoneService
import com.example.core_data.api.toFailedEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ServiceHandphoneRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val serviceHandphoneService: ServiceHandphoneService
) {
    fun insertServiceHandphone(orderTechnician: ServiceHandphoneRequest) : Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphone

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.insertServiceHandphone(orderTechnician)
            }

            val apiEvent: ApiEvent<CommonResponse?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response) {
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
}