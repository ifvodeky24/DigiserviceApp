package com.example.core_data.domain.auth

data class Auth(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val aksesId: Int = 0,
    val level: String = "",
    val teknisiId: Int = 0,
    val pelangganId: Int = 0,
    val hp: String = "",
    val namaToko: String = "",
    val alamat: String = "",
    val foto: String = "",
    val lat: String = "",
    val lng: String = "",
    val deskripsi: String = "",
    val isLogin: Boolean = false,
)

val Auth.isTechnician
    get() = level == ROLE_TECHNICIAN

val Auth.isConsument
    get() = level == ROLE_CONSUMENT

private const val ROLE_TECHNICIAN = "teknisi"
private const val ROLE_CONSUMENT = "pelanggan"