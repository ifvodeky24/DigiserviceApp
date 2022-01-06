package com.example.core_navigation

import android.content.Context
import android.content.Intent

enum class ActivityClassPath(private val className: String) {
    Auth("$BASE_PATH.feature_auth.AuthActivity"),
    Home("$BASE_PATH.feature_home.HomeActivity"),
    Profile("$BASE_PATH.feature_profile.HomeActivity"),
    History("$BASE_PATH.feature_history.HomeActivity"),
    Service("$BASE_PATH.feature_service.ServiceActivity"),
    Map("$BASE_PATH.feature_auth.register.MapsActivity"),
    Chat("$BASE_PATH.feature_chat.ChatActivity"),
    Product("$BASE_PATH.feature_product.ProductActivity");

    fun getIntent(context: Context) = Intent(context, Class.forName(className))
}

private const val BASE_PATH = "com.example"