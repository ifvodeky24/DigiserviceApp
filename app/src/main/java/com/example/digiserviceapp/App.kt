package com.example.digiserviceapp

import android.app.Application
import io.armcha.debugBanner.Banner
import io.armcha.debugBanner.DebugBanner
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        when (BuildConfig.BUILD_TYPE){
            "debug" -> "DEV"
            else -> ""
        }.let {
            val banner = Banner(bannerText = it, bannerColorRes = R.color.colorEnvBanner)
            DebugBanner.init(this, banner)
        }

        startKoin {
            androidContext(this@App)
        }
    }
}