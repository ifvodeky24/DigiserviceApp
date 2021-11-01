package com.example.core_data.domain.auth

data class Auth(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val aksesId: Int = 0,
    val level: String = "",
    val teknisiId: Int = 0,
    val isLogin: Boolean = false,
)