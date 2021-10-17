package com.example.core_navigation

import android.content.Context
import android.content.Intent

enum class ActivityClassPath(private val className: String) {
    Auth("$BASE_PATH.feature_auth.AuthActivity");

    fun getIntent(context: Context) = Intent(context, Class.forName(className))
}
private const val BASE_PATH = "com.example"