package com.example.core_data.api.service

import com.example.core_data.api.request.RequestAddServiceHandphone
import com.example.core_data.api.request.RequestUpdateServiceHandphone
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.servicehp.ServiceHandphoneTechnicianGetAllResponse
import com.example.core_data.api.response.servicehp.ServiceHandphoneTechnicianGetResponse
import retrofit2.http.*

interface ServiceHandphoneService {

    @FormUrlEncoded
    @POST(ServiceHandphoneInsert)
    suspend fun insertServiceHandphone(
        @Field("teknisi_id") teknisiId: Int,
        @Field("pelanggan_id") pelangganId: Int,
        @Field("jenis_hp") jenisHp: String,
        @Field("jenis_kerusakan") jenisKerusakan: String,
        @Field("by_kurir") byKurir: Int,
    ) : CommonResponse

    @FormUrlEncoded
    @PUT(ServiceHandphoneUpdate)
    suspend fun updateServiceHandphone(
        @Path(value = "service_handphone_id") technician_id: Int,
        @Field("status_service") statusService: String
    ) : CommonResponse

    @GET(ServiceHandphoneGetByTechnician)
    suspend fun getServiceHeadphoneByTechnician(
        @Path(value = "technician_id") technicianId: Int
    ) : ServiceHandphoneTechnicianGetAllResponse

    @GET(ServiceHandphoneGetById)
    suspend fun getServiceHeadphoneById(
        @Path(value = "service_handphone_id") technicianId: Int
    ) : ServiceHandphoneTechnicianGetResponse

    companion object {

        const val ServiceHandphone = "service-handphone"
        const val ServiceHandphoneInsert = "service-handphone-insert"
        const val ServiceHandphoneGetByTechnician = "service-handphone-by-teknisi/{technician_id}"
        const val ServiceHandphoneGetById = "service-handphone-by-id/{service_handphone_id}"
        const val ServiceHandphoneUpdate = "service-handphone-update/{service_handphone_id}"
    }
}