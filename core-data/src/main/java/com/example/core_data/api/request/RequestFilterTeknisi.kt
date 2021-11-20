package com.example.core_data.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestFilterTeknisi(
    @Json(name = "jenis_hp")
    val jenisHp: List<Int>,
    @Json(name = "jenis_kerusakan")
    val jenisKerusakan: List<Int>
)