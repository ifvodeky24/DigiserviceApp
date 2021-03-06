package com.example.core_data

import android.app.Application
import androidx.room.Room
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.example.core_data.BuildConfig.*
import com.example.core_data.api.ApiExecutor
import com.example.core_data.api.apiClient
import com.example.core_data.api.apiClientFCM
import com.example.core_data.api.httpClient
import com.example.core_data.api.service.*
import com.example.core_data.api.service.AuthService
import com.example.core_data.api.service.FCMService
import com.example.core_data.api.service.TechnicianService
import com.example.core_data.repository.AuthRepository
import com.example.core_data.repository.ServiceHandphoneRepository
import com.example.core_data.repository.StoreRepository
import com.example.core_data.repository.TechnicianRepository
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.gotev.cookiestore.SharedPreferencesCookieStore
import net.gotev.cookiestore.okhttp.JavaNetCookieJar
import okhttp3.CookieJar
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.Koin
import org.koin.dsl.module
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.net.CookieStore

@Suppress("MaxLineLength")
val Application.dataModule
    get() = module {
        single<CookieStore> { SharedPreferencesCookieStore(applicationContext, COOKIE_NAME) }
        single<CookieHandler> { CookieManager(get(), CookiePolicy.ACCEPT_ALL) }
        single<CookieJar> { JavaNetCookieJar(get()) }
        single {
            httpClient(
                TIMEOUT,
                HttpLoggingInterceptor.Level.BASIC,
                get(),
                ChuckerInterceptor.Builder(get()).build()
            )
        }

        single { Moshi.Builder().build() }
        single { ApiExecutor(get()) }

        single { apiClient<AuthService>(BASE_URL, get()) }
        single { apiClient<TechnicianService>(BASE_URL, get()) }
        single { apiClient<StoreService>(BASE_URL, get()) }
        single { apiClient<ServiceHandphoneService>(BASE_URL, get()) }
        single { apiClientFCM<FCMService>(FCM_URL, get()) }

        single {
            Room.databaseBuilder(get(), CoreDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                .build()
        }
        single { get<CoreDatabase>().authDao() }
        single { get<CoreDatabase>().technicianDao() }

        single { AuthRepository(get(), get(), get(), get(), get(), get()) }
        single { TechnicianRepository(get(), get(), get(), get()) }
        single { StoreRepository(get(), get()) }
        single { ServiceHandphoneRepository(get(), get()) }
    }

private const val TIMEOUT = 30L

/**
 * Clear local database
 */
suspend fun Koin.clearAppData() {
    withContext(Dispatchers.IO) {
        getOrNull<CoreDatabase>()?.clearAllTables()
    }
}

fun CookieHandler.removeAll() {
    (this as CookieManager).cookieStore.removeAll()
}