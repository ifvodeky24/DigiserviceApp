package com.example.core_data.api.response

import com.example.core_data.domain.JenisHp
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class JenisHpResponse(
    val code: Int,
    val message: String,
    val result: ListJenisHpResponse,
)

@JsonClass(generateAdapter = true)
internal data class DataJenisHpResponse(
    @Json(name = "jenis_id")
    val jenisId: Int = 0,
    @Json(name = "jenis_nama")
    val jenisNama: String = "",
    @Json(name = "jenis_thumbnail")
    val jenisThumbnail: String = ""
)

internal typealias ListJenisHpResponse = List<DataJenisHpResponse>

internal fun ListJenisHpResponse.toDomain() = map {
    it.toDomain()
}

internal fun DataJenisHpResponse.toDomain() = JenisHp(
    jenisId = jenisId,
    jenisNama = jenisNama,
    jenisThumbnail = jenisThumbnail
)