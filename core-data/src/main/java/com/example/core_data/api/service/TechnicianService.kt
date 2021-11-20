package com.example.core_data.api.service

import com.example.core_data.api.request.RequestFilterTeknisi
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.FilterTeknisiResponse
import com.example.core_data.api.response.technician.NearbyTechnicianResponse
import com.example.core_data.api.response.technician.TechnicianGetAllResponse
import com.example.core_data.api.service.TechnicianService.Companion.SearchTechnicianBy
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface TechnicianService {

    @GET(GetTechnicianAll)
    suspend fun getTechnicianAll(): TechnicianGetAllResponse

    @GET(FindNearbyTechnician)
    suspend fun findNearbyTechnician(
        @Path(value = Latitude) latitude: String,
        @Path(value = Longitude) longitude: String
    ): NearbyTechnicianResponse


    @POST(SearchTechnicianBy)
    suspend fun searchByTechnician(@Body request: RequestFilterTeknisi) : FilterTeknisiResponse

    companion object {
        private const val Latitude = "latitude"
        private const val Longitude = "longitude"

        const val GetTechnicianAll = "teknisi-all"
        const val FindNearbyTechnician = "teknisi-find-nearby/{$Latitude}/{$Longitude}"

        const val SearchTechnicianBy = "teknisi-search-by"
    }
}