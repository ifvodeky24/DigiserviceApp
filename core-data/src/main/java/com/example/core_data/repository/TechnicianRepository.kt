package com.example.core_data.repository

import android.text.TextUtils.isEmpty
import com.example.core_data.api.ApiEvent
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.ApiResult
import com.example.core_data.api.response.technician.ListTechnicianGetAllResponse
import com.example.core_data.api.response.technician.TechnicianGetAllDataResponse
import com.example.core_data.api.response.technician.toDomain
import com.example.core_data.api.response.toDomain
import com.example.core_data.api.service.TechnicianService
import com.example.core_data.api.toFailedEvent
import com.example.core_data.domain.technician.ListNearbyTechnician
import com.example.core_data.domain.technician.ListTechnicianGetAll
import com.example.core_data.domain.technician.TechnicianGetAll
import com.example.core_data.persistence.dao.TechnicianDao
import com.example.core_data.persistence.entity.technician.toEntity
import com.squareup.moshi.Moshi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TechnicianRepository internal constructor(
    private val apiExecutor: ApiExecutor,
    private val technicianService: TechnicianService,
    private val dao: TechnicianDao,
    private val jsonParser: Moshi,
){

    fun technicianGetAll() : Flow<ApiEvent<ListTechnicianGetAll>> = flow {
        runCatching {
            val apiId = TechnicianService.GetTechnicianAll
            val apiResult = apiExecutor.callApi(apiId) {
                technicianService.getTechnicianAll()
            }

            val apiEvent: ApiEvent<ListTechnicianGetAll> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        dao.replace(this.toEntity())
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
            emit(it.toFailedEvent<ListTechnicianGetAll>())
        }
    }

    fun findNearbyTechnician(latitude: String, longitude: String) : Flow<ApiEvent<ListNearbyTechnician>> = flow {
        runCatching {
            val apiId = TechnicianService.FindNearbyTechnician
            val apiResult = apiExecutor.callApi(apiId) {
                technicianService.findNearbyTechnician(latitude, longitude)
            }

            val apiEvent: ApiEvent<ListNearbyTechnician> = when(apiResult){
                is ApiResult.OnFailed -> apiResult.exception.toFailedEvent()
                is ApiResult.OnSuccess -> with(apiResult.response.result){
                    toDomain().run {
                        dao.replaceFindNearbyTechnician(this.toEntity())
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
            emit(it.toFailedEvent<ListNearbyTechnician>())
        }
    }
}