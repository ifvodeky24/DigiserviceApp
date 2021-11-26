package com.example.core_data.api.service

import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.store.ProductDetailResponse
import com.example.core_data.api.response.store.ProductGetAllResponse
import com.example.core_data.api.service.StoreService.Companion.UploadProduct
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StoreService {
    @GET(GetProductAll)
    suspend fun getProductAll(): ProductGetAllResponse

    @GET(GetProductDetail)
    suspend fun getProductDetail(
        @Path(JualId) jualId: Int
    ) : ProductDetailResponse

    @Multipart
    @POST(UploadProduct)
    suspend fun uploadProduk(
        @Part("jual_judul") jualJudul: RequestBody,
        @Part("jual_deskripsi") jualDeskripsi: RequestBody,
        @Part("jual_harga") jualHarga: RequestBody,
        @Part("jual_user_id") jualUserId: RequestBody,
        @Part("jual_jenis_hp") jualJenisHp: RequestBody,
        @Part foto: MultipartBody.Part,
    ) : CommonResponse

    companion object {
        private const val JualId = "jual_id"

        const val GetProductAll = "produk-all"
        const val UploadProduct = "produk-insert"
        const val GetProductDetail = "produk-detail/{$JualId}"
    }
}