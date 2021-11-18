package com.example.core_data.domain.store

import com.squareup.moshi.Json

data class ProductGetAll(
    val jualId: Int = 0,
    val pathPhoto: String = "",
    val jualStatus: String = "",
    val jualTujuan: String = "",
    val jualUserId: Int = 0,
    val jualTglPenjualan: String = "",
    val jualHarga: Int = 0,
    val jualJudul: String = "",
    val jualDeskripsi: String = "",
    val jualJenisHp: Int = 0
)

typealias ListProductGetAll = List<ProductGetAll>