package com.example.core_data.api.service

import com.example.core_data.api.response.technician.TechnicianGetAllResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface TechnicianService {

    @GET(GetTechnicianAll)
    suspend fun getTechnicianAll(): TechnicianGetAllResponse

    companion object {
        const val GetTechnicianAll = "teknisi-all"
    }
}