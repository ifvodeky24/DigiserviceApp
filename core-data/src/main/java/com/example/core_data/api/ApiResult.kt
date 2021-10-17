package com.example.core_data.api

internal sealed class ApiResult<out T> {
    data class OnSuccess<out T>(val response: T) : ApiResult<T>()
    data class OnFailed(val exception: ApiException) : ApiResult<Nothing>()
}