package com.example.digiserviceapp

import android.app.Application
import com.example.core_data.dataModule
import com.example.feature_auth.authModule
import com.example.feature_home.homeModule
import com.example.feature_service.serviceModule
import io.armcha.debugBanner.Banner
import io.armcha.debugBanner.DebugBanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        when (BuildConfig.BUILD_TYPE) {
            "debug" -> "DEV"
            else -> ""
        }.let {
            val banner = Banner(bannerText = it, bannerColorRes = R.color.colorEnvBanner)
            DebugBanner.init(this, banner)
        }


        when(BuildConfig.BUILD_TYPE) {
            "debug" -> Timber.plant(Timber.DebugTree())
        }

        startKoin {
            androidContext(this@App)
            modules(
                listOf(
                    dataModule,
                    authModule,
                    homeModule,
                    serviceModule
                )
            )
        }
    }
}