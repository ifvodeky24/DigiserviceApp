package com.example.feature_service

import android.app.Application
import com.example.feature_service.service_dialog.OrderTechnicianViewModel
import com.example.feature_service.service_detail.ServiceDetailViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@Suppress("UNUSED")
val Application.serviceModule
    get() = module {
        viewModel { ServiceViewModel(get(), get()) }
        viewModel { ServiceDetailViewModel(get()) }
        viewModel { OrderTechnicianViewModel(get()) }
    }