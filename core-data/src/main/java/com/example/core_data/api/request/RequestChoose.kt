package com.example.core_data.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestChoose(
    @Json(name = "teknisi_id")
    val teknisiId: Int,
    val deskripsi: String,
    @Json(name = "jenis_hp")
    val jenisHp: List<JenisHpRequest>,
    @Json(name = "jenis_kerusakan_hp")
    val jenisKerusakan: List<JenisKerusakanRequest>
)

@JsonClass(generateAdapter = true)
data class JenisHpRequest(
    val id: Int = 0,
    @Json(name = "jenis_hp_id")
    val jenisHpId: Int
)

@JsonClass(generateAdapter = true)
data class JenisKerusakanRequest(
    val id: Int = 0,
    @Json(name = "kerusakan_jenis_hp_id")
    val kerusakanJenisHpId: Int
)
