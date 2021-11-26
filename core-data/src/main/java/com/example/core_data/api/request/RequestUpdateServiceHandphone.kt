package com.example.core_data.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestUpdateServiceHandphone(
    @Json(name = "service_handphone_id")
    var serviceHandphoneId: Int = 0,
    @Json(name = "status_service")
    var statusService: String = "",
)
