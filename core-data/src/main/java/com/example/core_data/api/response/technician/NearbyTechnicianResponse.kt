package com.example.core_data.api.response.technician

import com.example.core_data.domain.technician.NearbyTechnician
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class NearbyTechnicianResponse(
    val code: Int,
    val result: ListNearbyTechnicianResponse,
    val message: String
)

@JsonClass(generateAdapter = true)
internal data class NearbyTechnicianDataResponse(
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
    val teknisiTotalScore: Double = 0.0,
    @Json(name = "teknisi_total_responden")
    val teknisiTotalResponden: Double = 0.0,
    @Json(name = "teknisi_deskripsi")
    val teknisiDeskripsi: String = "",
    @Json(name = "teknisi_foto")
    val teknisiFoto: String = "",
    @Json(name = "teknisi_sertifikat")
    val teknisiSertifikat: String = "",
    @Json(name = "jenis_id")
    val jenisId: Int = 0,
    @Json(name = "jenis_nama")
    val jenisNama: String = "",
    @Json(name = "jenis_thumbnail")
    val jenisThumbnail: String = "",
    @Json(name = "id_jenis_kerusakan")
    val idJenisKerusakan: Int = 0,
    @Json(name = "nama_kerusakan")
    val namaKerusakan: String = "",
    @Json(name = "deskripsi_kerusakan")
    val deskripsiKerusakan: String = "",
    @Json(name = "distance")
    val distance: Int = 0,
)

internal typealias ListNearbyTechnicianResponse = List<NearbyTechnicianDataResponse>

internal fun ListNearbyTechnicianResponse.toDomain() = map {
    it.toDomain()
}

internal fun NearbyTechnicianDataResponse.toDomain() = NearbyTechnician(
    teknisiId = teknisiId,
    email = email,
    teknisiNama = teknisiNama,
    teknisiNamaToko = teknisiNamaToko,
    teknisiAlamat = teknisiAlamat,
    teknisiLat = teknisiLat,
    teknisiLng = teknisiLng,
    teknisiHp = teknisiHp,
    createdAt = createdAt,
    updatedA = updatedA,
    teknisiTotalScore = teknisiTotalScore,
    teknisiTotalResponden = teknisiTotalResponden,
    teknisiDeskripsi = teknisiDeskripsi,
    teknisiFoto = teknisiFoto,
    teknisiSertifikat = teknisiSertifikat,
    jenisId = jenisId,
    jenisNama = jenisNama,
    jenisThumbnail = jenisThumbnail,
    idJenisKerusakan = idJenisKerusakan,
    namaKerusakan = namaKerusakan,
    deskripsiKerusakan = deskripsiKerusakan,
    distance = distance
)