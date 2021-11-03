package com.example.core_data.api.response.auth

import com.example.core_data.domain.auth.Auth
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class LoginResponse(
    val code: Int,
    val result: LoginDataResponse
)

@JsonClass(generateAdapter = true)
internal data class LoginDataResponse(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    @Json(name = "akses_id")
    val aksesId: Int = 0,
    val level: String = "",
    @Json(name = "teknisi_id")
    val teknisiId: Int = 0,
)

internal fun LoginDataResponse.toDomain() = Auth(
    id = id,
    name = name,
    email = email,
    password = password,
    aksesId = aksesId,
    level = level,
    teknisiId = teknisiId
)

//internal fun LoginDataResponse.toDomain(auth: Auth) =
//    auth.copy(id = id, name = name, email = email, password = password, aksesId = aksesId, level = level, teknisiId = teknisiId)