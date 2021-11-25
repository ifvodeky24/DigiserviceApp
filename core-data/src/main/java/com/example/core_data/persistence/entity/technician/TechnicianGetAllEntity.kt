package com.example.core_data.persistence.entity.technician

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core_data.domain.technician.ListTechnicianGetAll
import com.example.core_data.domain.technician.TechnicianGetAll

@Entity
data class TechnicianGetAllEntity(
    @PrimaryKey()
    val teknisiId: Int = 0,
    val email: String = "",
    val teknisiNama: String = "",
    val teknisiNamaToko: String = "",
    val teknisiAlamat: String = "",
    val teknisiLat: String = "",
    val teknisiLng: String = "",
    val teknisiHp: String = "",
    val createdAt: String = "",
    val updatedA: String = "",
    val teknisiTotalScore: Double = 0.0,
    val teknisiTotalResponden: Double = 0.0,
    val teknisiDeskripsi: String = "",
    val teknisiFoto: String = "",
    val teknisiSertifikat: String = ""
)

internal typealias TechnicianGetAllEntities = List<TechnicianGetAllEntity>

internal fun TechnicianGetAllEntity.toDomain() =
    TechnicianGetAll(
        teknisiId = teknisiId,
        email = email,
        teknisiNama = teknisiNama,
        teknisiNamaToko = teknisiNamaToko,
        teknisiAlamat = teknisiAlamat,
        teknisiLat = teknisiLat,
        teknisiLng = teknisiLng,
        teknisiHp = teknisiHp,
        createdAt = createdAt,
        updatedA = updatedA,
        teknisiTotalScore = teknisiTotalScore,
        teknisiTotalResponden = teknisiTotalResponden,
        teknisiDeskripsi = teknisiDeskripsi,
        teknisiFoto = teknisiFoto,
        teknisiSertifikat = teknisiSertifikat
    )

internal fun TechnicianGetAllEntities.toDomain() =
    map { it.toDomain() }

internal fun TechnicianGetAll.toEntity() =
    TechnicianGetAllEntity(
        teknisiId = teknisiId,
        email = email,
        teknisiNama = teknisiNama,
        teknisiNamaToko = teknisiNamaToko,
        teknisiAlamat = teknisiAlamat,
        teknisiLat = teknisiLat,
        teknisiLng = teknisiLng,
        teknisiHp = teknisiHp,
        createdAt = createdAt,
        updatedA = updatedA,
        teknisiTotalScore = teknisiTotalScore,
        teknisiTotalResponden = teknisiTotalResponden,
        teknisiDeskripsi = teknisiDeskripsi,
        teknisiFoto = teknisiFoto,
        teknisiSertifikat = teknisiSertifikat
    )

internal fun ListTechnicianGetAll.toEntity() =
    map { it.toEntity() }