package com.example.core_data.api.request

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RequestUpdateTeknisi(
    val user: UserRequest,
    val teknisi: TeknisiRequest,
    @Json(name = "jenis_kerusakan_hp")
    val jenisKerusakanHp: ListJenisKerusakanRequest,
    @Json(name = "jenis_hp")
    val jenisHp: ListJenisHpRequest
)

typealias ListJenisKerusakanRequest = List<JenisKerusakanRequest>

typealias ListJenisHpRequest = List<JenisHpRequest>

@JsonClass(generateAdapter = true)
data class UserRequest(
    val id: Int,
    val nama: String,
    val email: String,
    @Json(name = "no_hp")
    val noHp: String
)

@JsonClass(generateAdapter = true)
data class TeknisiRequest(
    @Json(name = "teknisi_id")
    val teknisiId: Int,
    @Json(name = "nama_toko")
    val namaToko: String,
    @Json(name = "teknisi_alamat")
    val teknisiAlamat: String,
    val deskripsi: String
)
