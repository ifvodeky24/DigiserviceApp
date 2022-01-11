package com.example.core_data.api.service

import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

internal interface FCMService {

    @POST(Send)
    suspend fun sendMessage(
        @HeaderMap headers: HashMap<String, String>,
        @Body messageBody: String
    ): String

    companion object {
        const val Send = "send"
    }
}