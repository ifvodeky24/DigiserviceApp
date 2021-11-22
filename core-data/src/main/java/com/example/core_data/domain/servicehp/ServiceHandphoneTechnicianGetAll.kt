package com.example.core_data.domain.servicehp

import com.squareup.moshi.Json

data class ServiceHandphoneTechnicianGetAll(
    val pelangganId: Int = 0,
    val serviceHandphoneId: Int = 0,
    val jenisKerusakan: String = "",
    val createdAt: String = "",
    val pelangganLat: String = "",
    val teknisiId: Int = 0,
    val pelangganLng: String = "",
    val pelangganFoto: String = "",
    val pelangganDateCreated: String = "",
    val jenisHp: String = "",
    val byKurir: Int = 0,
    val pelangganNama: String = "",
    val pelangganHp: String = "",
    val pelangganAlamat: String = "",
    val email: String = ""
)

typealias ListServiceHandphoneTechnicianGetAll = List<ServiceHandphoneTechnicianGetAll>
