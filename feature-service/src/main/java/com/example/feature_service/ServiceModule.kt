package com.example.feature_service

import android.app.Application
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@Suppress("UNUSED")
val Application.serviceModule
    get() = module {
        viewModel { ServiceViewModel(get(), get()) }
        viewModel { ServiceDetailViewModel(get()) }
        viewModel { OrderTechnicianViewModel(get(), get()) }
    }