package com.example.core_data.api

import com.example.core_data.api.response.ErrorResponse
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import retrofit2.HttpException
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketTimeoutException

internal class ApiExecutor(private val jsonParser: Moshi) {

    private val errorResponseAdapters: List<JsonAdapter<ErrorResponse>> by lazy {
        listOf(jsonParser.getAdapter())
    }

    internal suspend fun <S> callApi(
        @Suppress("UNUSED_PARAMETER") apiId: String = "",
        errorAdapters: List<JsonAdapter<*>> = emptyList(),
        apiCall: suspend () -> S
    ): ApiResult<S> = runCatching {
        ApiResult.OnSuccess(apiCall.invoke()!!)
    }.getOrElse {
        ApiResult.OnFailed(
            when (it) {
                is SocketTimeoutException,
                is InterruptedIOException -> ApiException.Timeout
                is ConnectException -> ApiException.Offline
                is IOException -> ApiException.Network
                is HttpException -> it.toFailedResponse(errorAdapters + errorResponseAdapters)
                is IllegalArgumentException,
                is JsonDataException -> ApiException.InvalidResponse(it)
                else -> ApiException.Unknown(it)
            }

        )
    }

    private fun HttpException.toFailedResponse(
        errorAdapters: List<JsonAdapter<*>> = emptyList()
    ): ApiException.FailedResponse<*> {
        return response()?.errorBody()?.string()?.let { rawResponse ->
            errorAdapters.mapNotNull { errorAdapter ->
                runCatching {
                    ApiException.FailedResponse(errorAdapter.fromJson(rawResponse))
                }.getOrNull()
            }.firstOrNull() ?: ApiException.FailedResponse<Nothing>()
        } ?: ApiException.FailedResponse<Nothing>()
    }

}