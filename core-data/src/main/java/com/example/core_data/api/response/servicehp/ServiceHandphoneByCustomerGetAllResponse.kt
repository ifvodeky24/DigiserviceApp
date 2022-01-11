package com.example.core_data.api.response.servicehp

import com.example.core_data.domain.servicehp.ServiceHandphoneByCustomerGetAll
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceHandphoneByCustomerGetAllResponse(
	val result: ListServiceHandphoneCustomerGetAllResponse,
	val code: Int,
	val message: String
)

@JsonClass(generateAdapter = true)
data class ServiceHandphoneByCustomerGetAllDataResponse(
	@Json(name="pelanggan_id")
	val pelangganId: Int = 0,
	@Json(name="teknisi_total_score")
	val teknisiTotalScore: Int = 0,
	@Json(name="teknisi_sertifikat")
	val teknisiSertifikat: String = "",
	@Json(name="teknisi_lng")
	val teknisiLng: String = "",
	@Json(name="service_handphone_id")
	val serviceHandphoneId: Int = 0,
	@Json(name="jenis_kerusakan")
	val jenisKerusakan: String = "",
	@Json(name="deskripsi_kerusakan")
	val deskripsiKerusakan: String = "",
	@Json(name="created_at")
	val createdAt: String = "",
	@Json(name="teknisi_id")
	val teknisiId: Int = 0,
	@Json(name="teknisi_foto")
	val teknisiFoto: String = "",
	@Json(name="teknisi_nama")
	val teknisiNama: String = "",
	@Json(name="jenis_hp")
	val jenisHp: String = "",
	@Json(name="teknisi_deskripsi")
	val teknisiDeskripsi: String = "",
	@Json(name="updated_at")
	val updatedAt: String = "",
	@Json(name="by_kurir")
	val byKurir: Int = 0,
	@Json(name="teknisi_total_responden")
	val teknisiTotalResponden: Int = 0,
	@Json(name="teknisi_hp")
	val teknisiHp: String = "",
	@Json(name="teknisi_alamat")
	val teknisiAlamat: String = "",
	@Json(name="status_service")
	val statusService: String = "",
	@Json(name="teknisi_nama_toko")
	val teknisiNamaToko: String = "",
	@Json(name="teknisi_lat")
	val teknisiLat: String = "",
	@Json(name="email")
	val email: String = ""
)

typealias ListServiceHandphoneCustomerGetAllResponse = List<ServiceHandphoneByCustomerGetAllDataResponse>

fun ListServiceHandphoneCustomerGetAllResponse.toDomain() = map {
	it.toDomain()
}

fun ServiceHandphoneByCustomerGetAllDataResponse.toDomain() =
	ServiceHandphoneByCustomerGetAll(
		pelangganId = pelangganId,
		teknisiTotalScore = teknisiTotalScore,
		teknisiSertifikat = teknisiSertifikat,
		teknisiLng = teknisiLng,
		serviceHandphoneId = serviceHandphoneId,
		jenisKerusakan = jenisKerusakan,
		deskripsiKerusakan = deskripsiKerusakan,
		createdAt = createdAt,
		teknisiId = teknisiId,
		teknisiFoto = teknisiFoto,
		teknisiNama = teknisiNama,
		jenisHp = jenisHp,
		teknisiDeskripsi = teknisiDeskripsi,
		updatedAt = updatedAt,
		byKurir = byKurir,
		teknisiTotalResponden = teknisiTotalResponden,
		teknisiHp = teknisiHp,
		teknisiAlamat = teknisiAlamat,
		statusService = statusService,
		teknisiNamaToko = teknisiNamaToko,
		teknisiLat = teknisiLat,
		email = email
	)