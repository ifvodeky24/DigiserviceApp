package com.example.core_data.api.service

import com.example.core_data.api.response.CommonResponse
import com.example.core_data.api.response.store.ProductBuyHistoryGetAllResponse
import com.example.core_data.api.response.store.ProductDetailResponse
import com.example.core_data.api.response.store.ProductGetAllResponse
import com.example.core_data.api.service.StoreService.Companion.UpdateStatusProduct
import com.example.core_data.api.service.StoreService.Companion.UploadProduct
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface StoreService {
    @GET(GetProductAll)
    suspend fun getProductAll(): ProductGetAllResponse

    @GET(GetProductByUserId)
    suspend fun getProductByUserId(
        @Path("jual_user_id") userId: Int
    ): ProductGetAllResponse

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

    @GET(GetBuyProductHistoryByUserId)
    suspend fun getBuyProductHistoryByUserId(
        @Path("beli_pembeli") userId: Int
    ): ProductBuyHistoryGetAllResponse

    @Multipart
    @POST(UpdateProduct)
    suspend fun updateImageProduk(
        @Part("id") id: RequestBody,
        @Part("jual_judul") jualJudul: RequestBody,
        @Part("jual_deskripsi") jualDeskripsi: RequestBody,
        @Part("jual_harga") jualHarga: RequestBody,
        @Part("jual_user_id") jualUserId: RequestBody,
        @Part("jual_jenis_hp") jualJenisHp: RequestBody,
        @Part foto: MultipartBody.Part,
    ) : CommonResponse

    @FormUrlEncoded
    @POST(UpdateProduct)
    suspend fun updateProduk(
        @Field("id") id: Int,
        @Field("jual_judul") jualJudul: String,
        @Field("jual_deskripsi") jualDeskripsi: String,
        @Field("jual_harga") jualHarga: Int,
        @Field("jual_user_id") jualUserId: Int,
        @Field("jual_jenis_hp") jualJenisHp: Int,
    ) : CommonResponse

    @GET(DeleteProduct)
    suspend fun deleteProduct(
        @Path("id") id: Int
    ) : CommonResponse

    @FormUrlEncoded
    @POST(BuyProduct)
    suspend fun buyProduct(
        @Field("beli_jual_id") beliJualId: Int,
        @Field("beli_jasa_kurir") beliJasaKurir: String,
        @Field("beli_pembeli") beliPembeli: Int
    ) : CommonResponse

    @FormUrlEncoded
    @POST(UpdateStatusProduct)
    suspend fun updateStatusBeliProduct(
        @Path("beli_id") beliId: Int,
        @Field("beli_status") beliStatus: String
    ) : CommonResponse

    @FormUrlEncoded
    @POST(ReviewProduct)
    suspend fun reviewProduct(
        @Field("beli_id") beliId: Int,
        @Field("nilai") rating: Float,
        @Field("isi") desc: String
    ) : CommonResponse

    companion object {
        private const val JualId = "jual_id"

        const val GetProductAll = "produk-all"
        const val GetProductByUserId = "produk-by-user-id/{jual_user_id}"
        const val GetBuyProductHistoryByUserId = "history-beli-produk-by-user-id/{beli_pembeli}"
        const val UploadProduct = "produk-insert"
        const val UpdateProduct = "produk-update"
        const val GetProductDetail = "produk-detail/{$JualId}"
        const val UpdateStatusProduct = "update-status-beli-product/{beli_id}"
        const val DeleteProduct = "produk-delete/{id}"
        const val BuyProduct = "buy-product"
        const val ReviewProduct = "review"
    }
}