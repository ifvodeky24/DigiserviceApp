package com.example.feature_home

import android.app.Application
import com.example.feature_home.account.AccountViewModel
<<<<<<< HEAD
import com.example.feature_home.store.ProductViewModel
=======
import com.example.feature_home.service.ServiceViewModel
>>>>>>> 710135ebd67edb44cf991bd99d8f94c95ea730c3
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val Application.homeModule
    get() = module {
        viewModel { HomeViewModel(get()) }
        viewModel { AccountViewModel(get()) }
<<<<<<< HEAD
        viewModel { ProductViewModel(get()) }
=======
        viewModel { ServiceViewModel(get()) }
>>>>>>> 710135ebd67edb44cf991bd99d8f94c95ea730c3
    }