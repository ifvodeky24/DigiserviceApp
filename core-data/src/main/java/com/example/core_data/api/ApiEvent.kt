package com.example.core_data.api

sealed class ApiEvent<out DATA> {

    var hasBeenConsumed: Boolean = false
    val hasNotBeenConsumed: Boolean
        get() = hasBeenConsumed.not()

    fun markConsumed() {
        hasBeenConsumed = true
    }

    data class OnProgress<DATA>(val currentResult: OnSuccess<DATA>? = null) : ApiEvent<DATA>()

    data class OnSuccess<DATA>(
        private val data: DATA,
        val dataSource: DataSource,
        val cacheIndex: Int
    ) : ApiEvent<DATA>() {
        enum class DataSource { SERVER, CACHE }

        val isFromCache get() = dataSource == DataSource.CACHE
        val isFromServer get() = dataSource == DataSource.SERVER
        val isInitialCache get() = cacheIndex == 0

        fun getData(isConsumed: Boolean = true) = data.also {
            hasBeenConsumed = hasBeenConsumed || isConsumed
        }

        companion object {
            fun <DATA> fromCache(data: DATA, cacheIndex: Int = 0) = OnSuccess(
                data = data,
                dataSource = DataSource.CACHE,
                cacheIndex = cacheIndex
            )

            fun <DATA> fromServer(data: DATA) = OnSuccess(
                data = data,
                dataSource = DataSource.SERVER,
                cacheIndex = Int.MIN_VALUE
            )
        }
    }

    data class OnFailed<DATA>(
        private val exception: ApiException,
        val currentResult: OnSuccess<DATA>? = null
    ) : ApiEvent<DATA>() {
        fun getException(isConsumed: Boolean = true) = exception.also {
            hasBeenConsumed = hasBeenConsumed || isConsumed
        }
    }

}