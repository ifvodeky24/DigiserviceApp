package com.example.core_data.api.service

import com.example.core_data.api.response.store.ProductGetAllResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreService {
    @GET(GetProductAll)
    suspend fun getProductAll(): ProductGetAllResponse

    @GET(GetProductDetail)
    suspend fun getProductDetail(
        @Path(JualId) jualId: Int
    )

    companion object {
        private const val JualId = "jual_id"

        const val GetProductAll = "produk-all"
        const val GetProductDetail = "produk-detail/{$JualId}"
    }
}