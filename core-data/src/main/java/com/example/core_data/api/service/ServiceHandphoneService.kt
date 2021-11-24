package com.example.core_data.api.service

import com.example.core_data.api.request.RequestAddServiceHandphone
import com.example.core_data.api.request.RequestUpdateServiceHandphone
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.servicehp.ServiceHandphoneTechnicianGetAllResponse
import com.example.core_data.api.response.servicehp.ServiceHandphoneTechnicianGetResponse
import retrofit2.http.*

interface ServiceHandphoneService {

    @FormUrlEncoded
    @POST(ServiceHandphone)
    suspend fun insertServiceHandphone(
        @Body orderTechnician: RequestAddServiceHandphone
    ) : CommonResponse

    @FormUrlEncoded
    @PUT(ServiceHandphoneUpdate)
    suspend fun updateServiceHandphone(
        @Path(value = "service_handphone_id") technician_id: Int,
        @Field("status_service") statusService: String
    ) : CommonResponse

    @GET(ServiceHandphoneGetByTechnician)
    suspend fun getServiceHeadphoneByTechnician(
        @Path(value = "technician_id") technician_id: Int
    ) : ServiceHandphoneTechnicianGetAllResponse

    @GET(ServiceHandphoneGetById)
    suspend fun getServiceHeadphoneById(
        @Path(value = "service_handphone_id") technician_id: Int
    ) : ServiceHandphoneTechnicianGetResponse

    companion object {

        const val ServiceHandphone = "service-handphone"
        const val ServiceHandphoneGetByTechnician = "service-handphone-by-teknisi/{technician_id}"
        const val ServiceHandphoneGetById = "service-handphone-by-id/{service_handphone_id}"
        const val ServiceHandphoneUpdate = "service-handphone-update/{service_handphone_id}"
    }
}