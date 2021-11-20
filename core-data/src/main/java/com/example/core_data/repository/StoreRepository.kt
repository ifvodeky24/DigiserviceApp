package com.example.core_data.repository

import com.example.core_data.api.ApiEvent
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.response.store.toDomain
import com.example.core_data.api.service.StoreService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.store.ListProductGetAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class StoreRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val storeService: StoreService
) {
    fun productGetAll(): Flow<ApiEvent<ListProductGetAll>> = flow {
        runCatching {
            val apiId = StoreService.GetProductAll
            val apiResult = apiExecutor.callApi(apiId) {
                storeService.getProductAll()
            }

            val apiEvent: ApiEvent<ListProductGetAll> = when(apiResult) {
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
            emit(it.toFailedEvent<ListProductGetAll>())
        }
    }
}