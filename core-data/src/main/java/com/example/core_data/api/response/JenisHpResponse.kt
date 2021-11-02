package com.example.core_data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class LoginResponse(
    val kode: Int,
    val result: JenisHpResponse
)

@JsonClass(generateAdapter = true)
internal data class JenisHpResponse(
    @Json(name = "jenis_id")
    val jenisId: Int = 0,
    @Json(name = "jenis_nama")
    val jenisNama: String = "",
    @Json(name = "jenis_thumbnail")
    val jenisThumbnail: String = ""
)