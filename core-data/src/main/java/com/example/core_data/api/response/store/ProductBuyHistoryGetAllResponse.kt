package com.example.core_data.api.response.store

import com.example.core_data.domain.store.ProductBuyHistoryGetAll
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductBuyHistoryGetAllResponse(
	@Json(name="result")
	val result: ListProductBuyHistoryGetAllDataResponse,
	@Json(name="code")
	val code: Int = 0,
	@Json(name="message")
	val message: String = ""
)

@JsonClass(generateAdapter = true)
data class ProductBuyHistoryGetAllDataResponse(
	@Json(name="jual_id")
	val jualId: Int = 0,
	@Json(name="beli_pembeli")
	val beliPembeli: Int = 0,
	@Json(name="jual_harga")
	val jualHarga: Int = 0,
	@Json(name="jual_judul")
	val jualJudul: String = "",
	@Json(name="path_photo")
	val pathPhoto: String = "",
	@Json(name="jual_user_id")
	val jualUserId: Int = 0,
	@Json(name="beli_jual_id")
	val beliJualId: Int = 0,
	@Json(name="beli_tgl_beli")
	val beliTglBeli: String = "",
	@Json(name="id")
	val id: Int = 0,
	@Json(name="teknisi_lat")
	val teknisiLat: String = "",
	@Json(name="beli_jasa_kurir")
	val beliJasaKurir: String = "",
	@Json(name="beli_id")
	val beliId: Int = 0,
	@Json(name="teknisi_id")
	val teknisiId: Int = 0,
	@Json(name="pelanggan_id")
	val pelangganId: Int = 0,
	@Json(name="beli_tgl_booking")
	val beliTglBooking: String = "",
	@Json(name="teknisi_nama")
	val teknisiNama: String = "",
	@Json(name="pelanggan_nama")
	val pelangganNama: String = "",
	@Json(name="beli_status")
	val beliStatus: String = "",
)

typealias ListProductBuyHistoryGetAllDataResponse = List<ProductBuyHistoryGetAllDataResponse>

fun ListProductBuyHistoryGetAllDataResponse.toDomain() = map {
	it.toDomain()
}

fun ProductBuyHistoryGetAllDataResponse.toDomain() =
	ProductBuyHistoryGetAll(
		jualId = jualId,
		beliPembeli = beliPembeli,
		jualHarga = jualHarga,
		jualJudul = jualJudul,
		pathPhoto = pathPhoto,
		jualUserId = jualUserId,
		beliJualId = beliJualId,
		beliTglBeli = beliTglBeli,
		id = id,
		teknisiLat = teknisiLat,
		beliJasaKurir = beliJasaKurir,
		beliId = beliId,
		teknisiId = teknisiId,
		pelangganId = pelangganId,
		beliTglBooking = beliTglBooking,
		teknisiNama = teknisiNama,
		pelangganNama = pelangganNama,
		beliStatus = beliStatus
	)