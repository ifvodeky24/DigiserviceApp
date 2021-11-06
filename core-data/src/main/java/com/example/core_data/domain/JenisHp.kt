package com.example.core_data.domain

data class JenisHp(
    val jenisId: Int = 0,
    val jenisNama: String = "",
    val jenisThumbnail: String = ""
)

typealias ListJenisHp = List<JenisHp>