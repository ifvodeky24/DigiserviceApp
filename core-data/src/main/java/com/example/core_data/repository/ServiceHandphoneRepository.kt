package com.example.core_data.repository

import com.example.core_data.api.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.servicehp.toDomain
import com.example.core_data.api.service.ServiceHandphoneService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.servicehp.ListServiceHandphoneByCustomerGetAll
import com.example.core_data.domain.servicehp.ListServiceHandphoneByTechnicianGetAll
import com.example.core_data.domain.servicehp.ServiceHandphoneByTechnicianGetAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

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

    fun getServiceHandphoneByTechnician(technicianId: Int): Flow<ApiEvent<ListServiceHandphoneByTechnicianGetAll?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneGetByTechnician

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.getServiceHandphoneByTechnician(technicianId)
            }

            val apiEvent: ApiEvent<ListServiceHandphoneByTechnicianGetAll?> = when(apiResult) {
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
            emit(it.toFailedEvent<ListServiceHandphoneByTechnicianGetAll>())
        }
    }

    fun getServiceHandphoneHistoryByTechnician(technicianId: Int): Flow<ApiEvent<ListServiceHandphoneByTechnicianGetAll?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneHistoryGetByTechnician

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.getServiceHandphoneHistoryByTechnician(technicianId)
            }

            val apiEvent: ApiEvent<ListServiceHandphoneByTechnicianGetAll?> = when(apiResult) {
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
            emit(it.toFailedEvent<ListServiceHandphoneByTechnicianGetAll>())
        }
    }


    fun getServiceHandphoneByCustomer(pelangganId: Int): Flow<ApiEvent<ListServiceHandphoneByCustomerGetAll?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneGetByTechnician

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.getServiceHandphoneByCustomer(pelangganId)
            }

            val apiEvent: ApiEvent<ListServiceHandphoneByCustomerGetAll?> = when(apiResult) {
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
            emit(it.toFailedEvent<ListServiceHandphoneByCustomerGetAll>())
        }
    }


    fun getServiceHandphoneById(serviceHandphoneId: Int): Flow<ApiEvent<ServiceHandphoneByTechnicianGetAll?>> = flow {
        runCatching {
            val apiId = ServiceHandphoneService.ServiceHandphoneGetById

            val apiResult = apiExecutor.callApi(apiId) {
                serviceHandphoneService.getServiceHandphoneById(serviceHandphoneId)
            }

            val apiEvent: ApiEvent<ServiceHandphoneByTechnicianGetAll?> = when(apiResult) {
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result) {
                    toDomain().run {
                        ApiEvent.OnSuccess.fromServer(this)
                    }
                }
            }

            emit(apiEvent)
        }.onFailure {
            emit(it.toFailedEvent<ServiceHandphoneByTechnicianGetAll>())
        }
    }

}