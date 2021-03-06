package com.example.core_data.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestAddServiceHandphone(
    @Json(name = "pelanggan_id")
    var pelangganId: Int = 0,
    @Json(name = "teknisi_id")
    var teknisiId: Int = 0,
    @Json(name = "jenis_hp")
    var jenisHp: String = "",
    @Json(name = "jenis_kerusakan")
    var jenisKerusakan: String = "",
    @Json(name = "deskripsi_kerusakan")
    var deskripsiKerusakan: String = "",
    @Json(name = "by_kurir")
    var byKurir: Int = 0
)