package com.example.core_data.domain

data class JenisKerusakan(
    val idKerusakan: Int = 0,
    val namaKerusakan: String = "",
    val deskripsiKerusakan: String = ""
)

typealias ListJenisKerusakan = List<JenisKerusakan>