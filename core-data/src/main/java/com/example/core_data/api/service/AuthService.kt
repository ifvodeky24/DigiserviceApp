package com.example.core_data.api.service

import com.example.core_data.api.request.RequestChoose
import com.example.core_data.api.request.RequestUpdateTeknisi
import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.JenisHpResponse
import com.example.core_data.api.response.JenisKerusakanResponse
import com.example.core_data.api.response.SkilsResponse
import com.example.core_data.api.response.auth.LoginResponse
import okhttp3.MultipartBody
import okhttp3.Request
import okhttp3.RequestBody
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

    @Multipart
    @POST(RegiterService)
    suspend fun registerService(
        @Part("teknisi_nama") teknisiNama: RequestBody,
        @Part("teknisi_hp") teknisiNoHp: RequestBody,
        @Part("password") password: RequestBody,
        @Part("email") email: RequestBody,
        @Part("teknisi_nama_toko") teknisiNamaToko: RequestBody,
        @Part("teknisi_alamat") teknisiAlamat: RequestBody,
        @Part("teknisi_lat") teknisiLat: RequestBody,
        @Part("teknisi_lng") teknisiLng: RequestBody,
        @Part("teknisi_deskripsi") teknisiDeskripsi: RequestBody,
        @Part("teknisi_total_score") teknisiTotalScore: RequestBody,
        @Part("teknisi_total_responden") teknisiTotalRespondent: RequestBody,
        @Part foto: MultipartBody.Part,
        @Part sertifikat: MultipartBody.Part,
        @Part identitas: MultipartBody.Part,
        @Part tempatUsaha: MultipartBody.Part
        ): CommonResponse

    @Multipart
    @POST(RegiterPelanggan)
    suspend fun registerPelanggan(
        @Part("pelanggan_nama") pelangganNama: RequestBody,
        @Part("pelanggan_hp") pelangganNoHp: RequestBody,
        @Part("password") password: RequestBody,
        @Part("email") email: RequestBody,
        @Part("pelanggan_alamat") pelangganAlamat: RequestBody,
        @Part("pelanggan_lat") pelangganLat: RequestBody,
        @Part("pelanggan_lng") pelangganLng: RequestBody,
        @Part foto: MultipartBody.Part,
        @Part identitas: MultipartBody.Part
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

    @POST(UpdateTeknisi)
    suspend fun updateTeknisi(@Path("id") id: Int, @Body request: RequestUpdateTeknisi) : CommonResponse

    @FormUrlEncoded
    @POST(UpdatePelanggan)
    suspend fun updatePelanggan(
        @Path("id") id: Int,
        @Field("pelanggan_id") pelangganId: Int,
        @Field("pelanggan_email") pelangganEmail: String,
        @Field("pelanggan_nama") pelangganNama: String,
        @Field("pelanggan_alamat") pelangganAlamat: String,
        @Field("pelanggan_hp") pelangganHp: String
    ): CommonResponse

    @GET(JenisKerusakanHp)
    suspend fun getJenisKerusakanHpAll(): SkilsResponse

    @Multipart
    @POST(UpdatePhotoTeknisi)
    suspend fun updatePhotoTeknisi(
        @Path("teknisi_id") teknisiId: Int,
        @Part foto: MultipartBody.Part,
    ) : CommonResponse

    @Multipart
    @POST(UpdatePhotoPelanggan)
    suspend fun updatePhotoPelanggan(
        @Path("pelanggan_id") pelangganId: Int,
        @Part foto: MultipartBody.Part,
    ) : CommonResponse

    @Multipart
    @POST(UpdateSertifikatTeknisi)
    suspend fun updateSertifikatTeknisi(
        @Path("teknisi_id") teknisiId: Int,
        @Part foto: MultipartBody.Part,
    ) : CommonResponse

    @Multipart
    @POST(UpdatePhotoIdentitas)
    suspend fun updatePhotoIdentitasTeknisi(
        @Path("teknisi_id") teknisiId: Int,
        @Part fotoIdentitas: MultipartBody.Part
    ) : CommonResponse

    @Multipart
    @POST(UpdatePhotoTempatUsaha)
    suspend fun updatePhotoTempatUsahaTeknisi(
        @Path("teknisi_id") teknisiId: Int,
        @Part fotoTempatUsaha: MultipartBody.Part
    ) : CommonResponse

    companion object {
        const val Login = "login"
        const val GetJenisKerusakanAll = "jenis-kerusakan-all"
        const val GetJenisHpAll = "jenis-hp-all"
        const val RegiterService = "teknisi-insert"
        const val RegiterPelanggan = "pelanggan-insert"
        const val UpdatePelanggan = "pelanggan-update/{id}"
        const val SaveChoose = "insert-teknisi-jenis-hp-keahlian"
        const val SkilsBy = "keahlian-teknisi-by/{teknisi_id}"
        const val JenisHpBy = "jenis-hp-by/{teknisi_id}"
        const val UpdateTeknisi = "teknisi-update/{id}"
        const val JenisKerusakanHp = "get-jenis-kerusakan-hp"
        const val UpdatePhotoTeknisi = "update-teknisi-foto/{teknisi_id}"
        const val UpdatePhotoPelanggan = "update-pelanggan-foto/{pelanggan_id}"
        const val UpdateSertifikatTeknisi = "update-teknisi-sertifikat/{teknisi_id}"
        const val UpdatePhotoIdentitas = "update-teknisi-identitas/{teknisi_id}"
        const val UpdatePhotoTempatUsaha = "update-teknisi-tempat-usaha/{teknisi_id}"
    }
}