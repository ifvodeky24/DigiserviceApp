package com.example.core_data.api.service

import com.example.core_data.api.request.ServiceHandphoneRequest
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.servicehp.ServiceHandphoneTechnicianGetAllResponse
import com.example.core_data.api.service.ServiceHandphoneService.Companion.ServiceHandphoneGetByTechnician
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceHandphoneService {

    @POST(ServiceHandphone)
    suspend fun insertServiceHandphone(
        @Body orderTechnician: ServiceHandphoneRequest
    ) : CommonResponse

    @GET(ServiceHandphoneGetByTechnician)
    suspend fun getServiceHeadphoneByTechnician(
        @Path(value = TechnicianId) technicianId: Int
    ) : ServiceHandphoneTechnicianGetAllResponse

    companion object {
        private const val TechnicianId = "technicianId"

        const val ServiceHandphone = "service-handphone"
        const val ServiceHandphoneGetByTechnician = "service-handphone/{$TechnicianId}"
    }
}