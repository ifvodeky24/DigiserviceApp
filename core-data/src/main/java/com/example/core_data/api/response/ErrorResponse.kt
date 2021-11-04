package com.example.core_data.api.response

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val code: Int = 0,
    val message: String = ""
)
