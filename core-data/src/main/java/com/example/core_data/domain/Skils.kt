package com.example.core_data.domain

data class Skils(
    val id: Int = 0,
    val teknisiKerusakanJenisHpId: Int,
    val jenisKerusakanHpId: Int,
    val teknisiNama: String = "",
    val namaKerusakan: String = ""
)

data class ResultSkils(
    val skils: ListSkils = emptyList(),
    val jenisHp: ListJenisHp = emptyList()
)

typealias ListSkils = List<Skils>