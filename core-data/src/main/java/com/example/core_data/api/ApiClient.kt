package com.example.core_data.api

import com.example.core_data.BuildConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.CookieJar
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

internal inline fun <reified T> apiClient(
    baseUrl: String,
    apiClient: OkHttpClient
) = Retrofit.Builder()
    .baseUrl(baseUrl)
    .client(apiClient)
    .addConverterFactory(MoshiConverterFactory.create())
    .build()
    .create(T::class.java)

internal fun httpClient(
    timeOut: Long,
    loggingLevel: HttpLoggingInterceptor.Level,
    cookieJar: CookieJar,
    vararg interceptors: Interceptor = emptyArray()
): OkHttpClient = OkHttpClient.Builder()
    .cookieJar(cookieJar)
    .addInterceptor(HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            // development build
            loggingLevel
        } else {
            // production build
            HttpLoggingInterceptor.Level.NONE
        }
    })
    .apply {
        interceptors.forEach {
            addInterceptor(it)
        }
    }
    .callTimeout(timeOut, TimeUnit.SECONDS)
    .readTimeout(timeOut, TimeUnit.SECONDS)
    .connectTimeout(timeOut, TimeUnit.SECONDS)
    .writeTimeout(timeOut, TimeUnit.SECONDS)
    .build()


internal inline fun <reified T> Moshi.getAdapter() =
    adapter(T::class.java)

internal inline fun <reified T> Moshi.getListAdapter() =
    adapter<List<T>>(Types.newParameterizedType(List::class.java, T::class.java))

internal const val ACCEPT_JSON = "Accept: application/json"