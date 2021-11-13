package com.example.feature_home

import android.app.Application
import com.example.feature_home.account.AccountViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Application.homeModule
    get() = module {
        viewModel { HomeViewModel(get()) }
        viewModel { AccountViewModel(get()) }
    }