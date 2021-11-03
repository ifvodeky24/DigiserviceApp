package com.example.core_data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonResponse(
    @Json(name = "success")
    val success: Boolean = false,
    @Json(name = "message")
    val message: String = "",
)
