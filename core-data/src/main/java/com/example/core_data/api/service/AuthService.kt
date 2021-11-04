package com.example.core_data.api.service

import com.example.core_data.api.response.JenisHpResponse
import com.example.core_data.api.response.auth.LoginResponse
import retrofit2.http.*

internal interface AuthService {

    @FormUrlEncoded
    @POST(Login)
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("level") level: String,
    ): LoginResponse

    @GET(GetJenisHpAll)
    suspend fun getJenisHpAll(): JenisHpResponse

    companion object {
        const val Login = "login"
        const val GetJenisHpAll = "jenis-hp-all"
    }
}