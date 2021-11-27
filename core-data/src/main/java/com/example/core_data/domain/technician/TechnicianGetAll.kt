package com.example.core_data.domain.technician

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TechnicianGetAll(
    val teknisiId: Int = 0,
    val email: String = "",
    val teknisiNama: String = "",
    val teknisiNamaToko: String = "",
    val teknisiAlamat: String = "",
    val teknisiLat: String = "",
    val teknisiLng: String = "",
    val teknisiHp: String = "",
    val createdAt: String = "",
    val updatedAt: String = "",
    val teknisiTotalScore: Double = 0.0,
    val teknisiTotalResponden: Double = 0.0,
    val teknisiDeskripsi: String = "",
    val teknisiFoto: String = "",
    val teknisiSertifikat: String = ""
) : Parcelable

typealias ListTechnicianGetAll = List<TechnicianGetAll>