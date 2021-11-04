package com.example.core_data.api.response

import com.example.core_data.domain.JenisKerusakan
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class JenisKerusakanResponse(
    val code: Int,
    val status: String,
    val message: String,
    val result: ListJenisKerusakanResponse,
)

@JsonClass(generateAdapter = true)
internal data class DataJenisKerusakanResponse(
    @Json(name = "id_jenis_kerusakan")
    val idJenisKerusakan: Int = 0,
    @Json(name = "nama_kerusakan")
    val namaKerusakan: String = "",
    @Json(name = "deskripsi_kerusakan")
    val deskripsiKerusakan: String = ""
)

internal typealias ListJenisKerusakanResponse = List<DataJenisKerusakanResponse>

internal fun ListJenisKerusakanResponse.toDomain() = map {
    it.toDomain()
}

internal fun DataJenisKerusakanResponse.toDomain() = JenisKerusakan(
    idKerusakan = idJenisKerusakan,
    namaKerusakan = namaKerusakan,
    deskripsiKerusakan = deskripsiKerusakan
)