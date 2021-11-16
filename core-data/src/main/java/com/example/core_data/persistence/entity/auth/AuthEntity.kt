package com.example.core_data.persistence.entity.auth

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core_data.domain.auth.Auth

@Entity
internal data class AuthEntity(
    @PrimaryKey
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val aksesId: Int = 0,
    val level: String = "",
    val teknisiId: Int = 0,
    val isLogin: Boolean = false
)

internal fun AuthEntity.toDomain() =
    Auth(
        id = id,
        name = name,
        email = email,
        password = password,
        aksesId = aksesId,
        level = level,
        teknisiId = teknisiId,
        isLogin = isLogin
    )

internal fun Auth.toEntity() =
    AuthEntity(
        id = id,
        name = name,
        email = email,
        password = password,
        aksesId = aksesId,
        level = level,
        teknisiId = teknisiId,
        isLogin = isLogin
    )