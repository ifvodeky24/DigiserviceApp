package com.example.core_data.domain.servicehp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceHandphoneByCustomerGetAll(
    val pelangganId: Int = 0,
    val teknisiTotalScore: Int = 0,
    val teknisiSertifikat: String = "",
    val teknisiLng: String = "",
    val serviceHandphoneId: Int = 0,
    val jenisKerusakan: String = "",
    val deskripsiKerusakan: String = "",
    val createdAt: String = "",
    val teknisiId: Int = 0,
    val teknisiFoto: String = "",
    val teknisiNama: String = "",
    val jenisHp: String = "",
    val teknisiDeskripsi: String = "",
    val updatedAt: String = "",
    val byKurir: Int = 0,
    val teknisiTotalResponden: Int = 0,
    val teknisiHp: String = "",
    val teknisiAlamat: String = "",
    val statusService: String = "",
    val teknisiNamaToko: String = "",
    val teknisiLat: String = "",
    val email: String = ""
) : Parcelable

typealias ListServiceHandphoneByCustomerGetAll = List<ServiceHandphoneByCustomerGetAll>
