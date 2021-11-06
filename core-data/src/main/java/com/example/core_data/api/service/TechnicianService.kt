package com.example.core_data.api.service

import com.example.core_data.api.response.technician.TechnicianGetAllResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface TechnicianService {

    @GET(GetTechnicianAll)
    fun getTechnicianAll(
        @Path("param")param:String,
        @Path("order")order:String
    ): TechnicianGetAllResponse

    companion object {
        const val GetTechnicianAll = "teknisi-all"
    }
}