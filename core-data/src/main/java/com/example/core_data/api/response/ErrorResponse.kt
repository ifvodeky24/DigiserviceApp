package com.example.core_data.api.response

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    val error: ErrorResponseData
)

@JsonClass(generateAdapter = true)
data class ErrorResponseData(
    val status: Int = 0,
    val message: String = "",
    @Json(name = "error_codes")
    val errorCodes: List<Int> = emptyList()
)

infix fun ErrorResponse.contains(errorCode: Int) =
    error.errorCodes.contains(errorCode)
