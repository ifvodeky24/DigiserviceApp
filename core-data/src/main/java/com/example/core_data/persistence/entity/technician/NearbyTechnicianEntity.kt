package com.example.core_data.persistence.entity.technician

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.core_data.domain.technician.ListNearbyTechnician
import com.example.core_data.domain.technician.NearbyTechnician

@Entity
internal data class NearbyTechnicianEntity(
    @PrimaryKey
    val teknisiId: Int,
    val email: String,
    val teknisiNama: String,
    val teknisiNamaToko: String,
    val teknisiAlamat: String,
    val teknisiLat: String,
    val teknisiLng: String,
    val teknisiHp: String? = "",
    val createdAt: String,
    val updatedAt: String,
    val teknisiTotalScore: Double,
    val teknisiTotalResponden: Double,
    val teknisiDeskripsi: String,
    val teknisiFoto: String,
    val teknisiSertifikat: String,
    val distance: Double,
)

internal typealias NearbyTechnicianEntities = List<NearbyTechnicianEntity>

internal fun NearbyTechnicianEntity.toDomain() =
    NearbyTechnician(
        teknisiId = teknisiId,
        email = email,
        teknisiNama = teknisiNama,
        teknisiNamaToko = teknisiNamaToko,
        teknisiAlamat = teknisiAlamat,
        teknisiLat = teknisiLat,
        teknisiLng = teknisiLng,
        teknisiHp = teknisiHp,
        createdAt = createdAt,
        updatedAt = updatedAt,
        teknisiTotalScore = teknisiTotalScore,
        teknisiTotalResponden = teknisiTotalResponden,
        teknisiDeskripsi = teknisiDeskripsi,
        teknisiFoto = teknisiFoto,
        teknisiSertifikat = teknisiSertifikat,
        distance = distance
    )

internal fun NearbyTechnicianEntities.toDomain() =
    map { it.toDomain() }

internal fun NearbyTechnician.toEntity() =
    NearbyTechnicianEntity(
        teknisiId = teknisiId,
        email = email,
        teknisiNama = teknisiNama,
        teknisiNamaToko = teknisiNamaToko,
        teknisiAlamat = teknisiAlamat,
        teknisiLat = teknisiLat,
        teknisiLng = teknisiLng,
        teknisiHp = teknisiHp,
        createdAt = createdAt,
        updatedAt = updatedAt,
        teknisiTotalScore = teknisiTotalScore,
        teknisiTotalResponden = teknisiTotalResponden,
        teknisiDeskripsi = teknisiDeskripsi,
        teknisiFoto = teknisiFoto,
        teknisiSertifikat = teknisiSertifikat,
        distance = distance
    )

internal fun ListNearbyTechnician.toEntity() =
    map { it.toEntity() }