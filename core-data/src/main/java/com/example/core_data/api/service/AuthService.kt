package com.example.core_data.api.service

import com.example.core_data.api.request.LoginRequest
import com.example.core_data.api.response.auth.LoginResponse
import retrofit2.http.Body
import retrofit2.http.POST

internal interface AuthService {

    @POST(Login)
    suspend fun login(
        @Body body: LoginRequest
    ): LoginResponse

    companion object {
        const val Login = "login"
    }
}