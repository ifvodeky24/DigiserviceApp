package com.example.core_data.repository

import com.example.core_data.api.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.request.RequestAddServiceHandphone
import com.example.core_data.api.request.RequestUpdateServiceHandphone
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.servicehp.toDomain
import com.example.core_data.api.service.ServiceHandphoneService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.servicehp.ListServiceHandphoneTechnicianGetAll
import com.example.core_data.domain.servicehp.ServiceHandphoneTechnicianGetAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.http.Field

class ServiceHandphoneRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val serviceHandphoneService: ServiceHandphoneService
) {
    fun insertServiceHandphone(
        teknisiId: Int,
        pelangganId: Int,
        jenisHp: String,
        jenisKerusakan: String,
        byKurir: Int
    ) : Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneInsert

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.insertServiceHandphone(
                    teknisiId,
                    pelangganId,
                    jenisHp,
                    jenisKerusakan,
                    byKurir
                )
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

    fun updateServiceHandphone(serviceHandphoneId: Int, statusService: String): Flow<ApiEvent<CommonResponse?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneUpdate

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.updateServiceHandphone(serviceHandphoneId, statusService)
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

    fun getServiceHeadphoneByTechnician(technicianId: Int): Flow<ApiEvent<ListServiceHandphoneTechnicianGetAll?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneGetByTechnician

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.getServiceHeadphoneByTechnician(technicianId)
            }

            val apiEvent: ApiEvent<ListServiceHandphoneTechnicianGetAll?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result) {
                    toDomain().run {
                        if (isEmpty()) {
                            ApiEvent.OnSuccess.fromServer(emptyList())
                        } else {
                            ApiEvent.OnSuccess.fromServer(this)
                        }
                    }
                }
            }

            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ListServiceHandphoneTechnicianGetAll>())
        }
    }

    fun getServiceHeadphoneById(serviceHandphoneId: Int): Flow<ApiEvent<ServiceHandphoneTechnicianGetAll?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneGetById

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.getServiceHeadphoneById(serviceHandphoneId)
            }

            val apiEvent: ApiEvent<ServiceHandphoneTechnicianGetAll?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result) {
                    toDomain().run {
                        ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }

            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ServiceHandphoneTechnicianGetAll>())
        }
    }

}