package com.example.core_data.api.service

import com.example.core_data.api.request.RequestChoose
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.JenisHpResponse
import com.example.core_data.api.response.JenisKerusakanResponse
import com.example.core_data.api.response.SkilsResponse
import com.example.core_data.api.response.auth.LoginResponse
import retrofit2.http.*

internal interface AuthService {

    @FormUrlEncoded
    @POST(Login)
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET(GetJenisHpAll)
    suspend fun getJenisHpAll(): JenisHpResponse

    @GET(GetJenisKerusakanAll)
    suspend fun getJenisKerusakanAll(): JenisKerusakanResponse

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

    @GET(SkilsBy)
    suspend fun getCurrenSkils(
        @Path("teknisi_id") teknisiId: Int
    ): SkilsResponse

    @GET(JenisHpBy)
    suspend fun getCurrenJenisHp(
        @Path("teknisi_id") teknisiId: Int
    ): JenisHpResponse

    @POST(SaveChoose)
    suspend fun saveChoose(@Body request: RequestChoose) : CommonResponse

    companion object {
        const val Login = "login"
        const val GetJenisKerusakanAll = "jenis-kerusakan-all"
        const val GetJenisHpAll = "jenis-hp-all"
        const val RegiterService = "teknisi-insert"
        const val SaveChoose = "insert-teknisi-jenis-hp-keahlian"
        const val JenisKerusakanBy = "insert-teknisi-jenis-hp-keahlian"
        const val SkilsBy = "keahlian-teknisi-by/{teknisi_id}"
        const val JenisHpBy = "jenis-hp-by/{teknisi_id}"
    }
}