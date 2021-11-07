package com.example.core_data.api.service

import com.example.core_data.api.response.technician.NearbyTechnicianResponse
import com.example.core_data.api.response.technician.TechnicianGetAllResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface TechnicianService {

    @GET(GetTechnicianAll)
    suspend fun getTechnicianAll(): TechnicianGetAllResponse

    @GET(FindNearbyTechnician)
    suspend fun findNearbyTechnician(
        @Path(value = Latitude) latitude: String,
        @Path(value = Longitude) longitude: String
    ): NearbyTechnicianResponse

    companion object {
        private const val Latitude = "latitude"
        private const val Longitude = "longitude"

        const val GetTechnicianAll = "teknisi-all"
        const val FindNearbyTechnician = "teknisi-find-nearby/{$Latitude}/{$Longitude}"
    }
}