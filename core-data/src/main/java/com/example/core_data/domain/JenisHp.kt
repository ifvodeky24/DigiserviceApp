package com.example.core_data.domain

import com.example.core_data.api.request.JenisHpRequest

data class JenisHp(
    val jenisId: Int = 0,
    val jenisNama: String = "",
    val jenisThumbnail: String = "",
    val isChecked: Boolean = false,
    val value: String = ""
)

typealias ListJenisHp = List<JenisHp>

fun ListJenisHp.toRequest() = this.map { it.toRequest() }

fun JenisHp.toRequest() = JenisHpRequest(jenisHpId = this.jenisId)