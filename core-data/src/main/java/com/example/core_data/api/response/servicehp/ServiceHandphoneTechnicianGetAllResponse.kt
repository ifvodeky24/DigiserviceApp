package com.example.core_data.api.response.servicehp

import com.example.core_data.domain.servicehp.ServiceHandphoneTechnicianGetAll
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceHandphoneTechnicianGetAllResponse(
	val result: ListServiceHandphoneTechnicianGetAllDataResponse,
	val code: Int,
	val message: String
)

@JsonClass(generateAdapter = true)
data class ServiceHandphoneTechnicianGetAllDataResponse(
	@Json(name="pelanggan_id")
	val pelangganId: Int = 0,
	@Json(name="service_handphone_id")
	val serviceHandphoneId: Int = 0,
	@Json(name="jenis_kerusakan")
	val jenisKerusakan: String = "",
	@Json(name="created_at")
	val createdAt: String = "",
	@Json(name="pelanggan_lat")
	val pelangganLat: String = "",
	@Json(name="teknisi_id")
	val teknisiId: Int = 0,
	@Json(name="pelanggan_lng")
	val pelangganLng: String = "",
	@Json(name="pelanggan_foto")
	val pelangganFoto: String = "",
	@Json(name="pelanggan_date_created")
	val pelangganDateCreated: String = "",
	@Json(name="jenis_hp")
	val jenisHp: String = "",
	@Json(name="updated_at")
	val updatedAt: String = "",
	@Json(name="by_kurir")
	val byKurir: Int = 0,
	@Json(name="pelanggan_nama")
	val pelangganNama: String = "",
	@Json(name="pelanggan_hp")
	val pelangganHp: String = "",
	@Json(name="pelanggan_alamat")
	val pelangganAlamat: String = "",
	@Json(name="email")
	val email: String = ""
)

typealias ListServiceHandphoneTechnicianGetAllDataResponse = List<ServiceHandphoneTechnicianGetAllDataResponse>

fun ListServiceHandphoneTechnicianGetAllDataResponse.toDomain() = map {
	it.toDomain()
}

fun ServiceHandphoneTechnicianGetAllDataResponse.toDomain() =
	ServiceHandphoneTechnicianGetAll(
		pelangganId = pelangganId,
		serviceHandphoneId = serviceHandphoneId,
		jenisKerusakan = jenisKerusakan,
		createdAt = createdAt,
		pelangganLat = pelangganLat,
		teknisiId = teknisiId,
		pelangganLng = pelangganLng,
		pelangganFoto = pelangganFoto,
		pelangganDateCreated = pelangganDateCreated,
		jenisHp = jenisHp,
		byKurir = byKurir,
		pelangganNama = pelangganNama,
		pelangganHp = pelangganHp,
		pelangganAlamat = pelangganAlamat,
		email = email
	)