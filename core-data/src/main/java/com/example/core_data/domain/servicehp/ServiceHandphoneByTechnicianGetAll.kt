package com.example.core_data.domain.servicehp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceHandphoneByTechnicianGetAll(
    val pelangganId: Int = 0,
    val serviceHandphoneId: Int = 0,
    val jenisKerusakan: String = "",
    val deskripsiKerusakan: String = "",
    val createdAt: String = "",
    val pelangganLat: String = "",
    val teknisiId: Int = 0,
    val pelangganLng: String = "",
    val pelangganFoto: String = "",
    val pelangganDateCreated: String = "",
    val jenisHp: String = "",
    val byKurir: Int = 0,
    val statusService: String = "",
    val pelangganNama: String = "",
    val pelangganHp: String = "",
    val pelangganAlamat: String = "",
    val email: String = ""
) : Parcelable

typealias ListServiceHandphoneByTechnicianGetAll = List<ServiceHandphoneByTechnicianGetAll>
