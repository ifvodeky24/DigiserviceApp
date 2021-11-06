package com.example.core_data.api.response.technician

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class TechnicianGetAllResponse(
    val code: Int,
    val result: TechnicianGetAllDataResponse,
    val message: String
)

@JsonClass(generateAdapter = true)
internal data class TechnicianGetAllDataResponse(
    @Json(name = "teknisi_id")
    val teknisiId: Int = 0,
    val email: String = "",
    @Json(name = "teknisi_nama")
    val teknisiNama: String = "",
    @Json(name = "teknisi_nama_toko")
    val teknisiNamaToko: String = "",
    @Json(name = "teknisi_alamat")
    val teknisiAlamat: String = "",
    @Json(name = "teknisi_lat")
    val teknisiLat: String = "",
    @Json(name = "teknisi_lng")
    val teknisiLng: String = "",
    @Json(name = "teknisi_hp")
    val teknisiHp: String = "",
    @Json(name = "created_at")
    val createdAt: String = "",
    @Json(name = "updated_at")
    val updatedA: String = "",
    @Json(name = "teknisi_total_score")
    val teknisiTotal_score: Int = 0,
    @Json(name = "teknisi_total_responden")
    val teknisiTotal_responden: Int = 0,
    @Json(name = "teknisi_deskripsi")
    val teknisiDeskripsi: String = "",
    @Json(name = "teknisi_foto")
    val teknisiFoto: String = "",
    @Json(name = "teknisi_sertifikat")
    val teknisiSertifikat: String = ""
)