package com.example.core_data.api.response

import com.example.core_data.api.response.technician.ListTechnicianGetAllResponse
import com.example.core_data.domain.JenisHp
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class FilterTeknisiResponse(
    val code: Int,
    val status: String,
    val message: String,
    val result: ListTechnicianGetAllResponse,
)
