package com.example.core_data.api.service

import com.example.core_data.api.request.ServiceHandphoneRequest
import com.example.core_data.api.response.CommonResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ServiceHandphoneService {

    @POST(ServiceHandphone)
    suspend fun insertServiceHandphone(
        @Body orderTechnician: ServiceHandphoneRequest
    ) : CommonResponse

    companion object {
        const val ServiceHandphone = "service-handphone"
    }
}