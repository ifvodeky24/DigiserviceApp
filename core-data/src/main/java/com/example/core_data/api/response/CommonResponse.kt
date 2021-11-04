package com.example.core_data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CommonResponse(
    @Json(name = "code")
    val code: Int,
    @Json(name = "message")
    val message: String = "",
    @Json(name = "result")
    val result: String = "",
)
