package com.example.core_data.api.response.store

import com.example.core_data.domain.store.ProductDetail
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductDetailResponse(
    val result: ProductDetailDataResponse,
    val code: Int,
    val message: String
)

@JsonClass(generateAdapter = true)
data class ProductDetailDataResponse(
    @Json(name="jual_id")
    val jualId: Int = 0,
    @Json(name="jual_status")
    val jualStatus: String = "",
    @Json(name="jual_tujuan")
    val jualTujuan: String = "",
    @Json(name="jual_user_id")
    val jualUserId: Int = 0,
    @Json(name="jual_tgl_penjualan")
    val jualTglPenjualan: String = "",
    @Json(name="jual_harga")
    val jualHarga: Int=0,
    @Json(name="jual_judul")
    val jualJudul: String = "",
    @Json(name="jual_deskripsi")
    val jualDeskripsi: String = "",
    @Json(name="jual_jenis_hp")
    val jualJenisHp: Int = 0,
    @Json(name="jenis_nama")
    val jenisNama: String = "",
    @Json(name="jenis_thumbnail")
    val jenisThumbnail: String = "",
    @Json(name="name")
    val name: String = "",
    @Json(name="foto_produk")
    val fotoProduk: String = ""
)

fun ProductDetailDataResponse.toDomain() = ProductDetail(
    jualId = jualId,
    jualStatus = jualStatus,
    jualTujuan = jualTujuan,
    jualUserId = jualUserId,
    jualTglPenjualan = jualTglPenjualan,
    jualHarga = jualHarga,
    jualJudul = jualJudul,
    jualDeskripsi = jualDeskripsi,
    jualJenisHp = jualJenisHp,
    jenisNama = jenisNama,
    jenisThumbnail = jenisThumbnail,
    name = name,
    fotoProduk = fotoProduk
)