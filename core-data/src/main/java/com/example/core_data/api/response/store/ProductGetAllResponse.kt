package com.example.core_data.api.response.store

import com.example.core_data.domain.store.ProductGetAll
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductGetAllResponse(
	val result: ListProductGetAllDataResponse,
	val code: Int,
	val message: String
)

@JsonClass(generateAdapter = true)
data class ProductGetAllDataResponse(
	@Json(name="jual_id")
	val jualId: Int = 0,
	@Json(name = "foto_produk")
	val pathPhoto: String = "",
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
	val jualJenisHp: Int = 0
)

typealias ListProductGetAllDataResponse = List<ProductGetAllDataResponse>

internal fun ListProductGetAllDataResponse.toDomain() = map {
	it.toDomain()
}

fun ProductGetAllDataResponse.toDomain() = ProductGetAll(
	jualId = jualId,
	pathPhoto = pathPhoto,
	jualStatus = jualStatus,
	jualTujuan = jualTujuan,
	jualUserId = jualUserId,
	jualTglPenjualan = jualTglPenjualan,
	jualHarga = jualHarga,
	jualJudul = jualJudul,
	jualDeskripsi = jualDeskripsi,
	jualJenisHp = jualJenisHp
)