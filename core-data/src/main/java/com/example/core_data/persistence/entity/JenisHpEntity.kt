package com.example.core_data.persistence.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core_data.domain.JenisHp
import com.example.core_data.domain.auth.Auth

@Entity
internal data class JenisHpEntity(
    @PrimaryKey
    val jenisId: Int,
    val jenisNama: String,
    val jenisThumbnail: String
)

internal fun JenisHpEntity.toDomain() =
    JenisHp(
        jenisId = jenisId,
        jenisNama = jenisNama,
        jenisThumbnail = jenisThumbnail
    )

internal fun JenisHpEntity.toEntity() =
    JenisHpEntity(
        jenisId = jenisId,
        jenisNama = jenisNama,
        jenisThumbnail = jenisThumbnail
    )