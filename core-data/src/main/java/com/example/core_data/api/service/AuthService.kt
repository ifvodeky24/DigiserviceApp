package com.example.core_data.api.service

import com.example.core_data.api.request.LoginRequest
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.JenisHpResponse
import com.example.core_data.api.response.auth.LoginResponse
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

internal interface AuthService {

    @POST(Login)
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    @GET(GetJenisHpAll)
    suspend fun getJenisHpAll(): JenisHpResponse

    @FormUrlEncoded
    @POST(RegiterService)
    suspend fun registerService(
        @Field("teknisi_nama") teknisiNama: String,
        @Field("password") password: String,
        @Field("email") email: String,
        @Field("teknisi_nama_toko") teknisiNamaToko: String,
        @Field("teknisi_alamat") teknisiAlamat: String,
        @Field("teknisi_lat") teknisiLat: Float,
        @Field("teknisi_lng") teknisiLng: Float,
        @Field("teknisi_deskripsi") teknisiDeskripsi: String,
    ): CommonResponse

    companion object {
        const val Login = "login"
        const val GetJenisHpAll = "jenis-hp-all"
        const val RegiterService = "teknisi-insert"
    }
}