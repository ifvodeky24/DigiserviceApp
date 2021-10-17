package com.example.core_data.api

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

inline val LiveData<ApiEvent<*>>.isOnProgress
    get() = value is ApiEvent.OnProgress<*>

inline val LiveData<ApiEvent<*>>.isSuccess
    get() = value is ApiEvent.OnSuccess<*>

inline val LiveData<ApiEvent<*>>.isFailed
    get() = value is ApiEvent.OnFailed<*>

infix fun ApiEvent<*>.sameTypeWith(other: ApiEvent<*>) =
    this is ApiEvent.OnProgress && other is ApiEvent.OnProgress ||
            this is ApiEvent.OnFailed<*> && other is ApiEvent.OnFailed<*> ||
            this is ApiEvent.OnSuccess<*> && other is ApiEvent.OnSuccess<*>

//endregion
//region ApiEvent Data Shortcut inside Live Data

fun <DATA> LiveData<ApiEvent<DATA>>.getSuccessEventOrNull(): ApiEvent.OnSuccess<DATA>? =
    value?.let {
        when (it) {
            is ApiEvent.OnSuccess -> it
            is ApiEvent.OnFailed -> it.currentResult
            is ApiEvent.OnProgress -> it.currentResult
        }
    }

fun <DATA> LiveData<ApiEvent<DATA?>>.getDataOrNull(isConsumed: Boolean = true): DATA? =
    value?.let {
        when (it) {
            is ApiEvent.OnSuccess<DATA?> -> it.getData(isConsumed)
            is ApiEvent.OnFailed<DATA?> -> it.currentResult?.getData(isConsumed)
            is ApiEvent.OnProgress<DATA?> -> it.currentResult?.getData(isConsumed)
        }
    }

@Suppress("UNCHECKED_CAST")
fun <DATA> LiveData<ApiEvent<List<DATA>>>.getDataOrEmpty(isConsumed: Boolean = true): List<DATA> =
    value?.let {
        when (it) {
            is ApiEvent.OnSuccess<List<DATA>> -> it.getData(isConsumed)
            is ApiEvent.OnProgress<List<DATA>> -> it.currentResult?.getData(isConsumed)
            is ApiEvent.OnFailed<List<DATA>> -> it.currentResult?.getData(isConsumed)
        } ?: emptyList()
    } ?: emptyList()

//endregion
//region ApiEvent Transformation inside Live Data

fun <DATA> MutableLiveData<ApiEvent<DATA>>.update(event: ApiEvent<DATA>) {
    value = if (event is ApiEvent.OnFailed<DATA>) {
        event.copy(currentResult = getSuccessEventOrNull())
    } else event
}

//endregion
//region ApiException Converter

internal fun <DATA> ApiException.toFailedEvent() =
    ApiEvent.OnFailed<DATA>(this, null)

internal inline fun <reified DATA> Throwable.toFailedEvent() =
    ApiEvent.OnFailed<DATA>(ApiException.Unknown(this), null)

//endregion