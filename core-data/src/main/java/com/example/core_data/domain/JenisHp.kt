package com.example.core_data.domain

import com.example.core_data.api.request.JenisHpRequest

data class JenisHp(
    val id: Int = 0,
    val teknisiJenisHpId: Int = 0,
    val jenisId: Int = 0,
    val jenisNama: String = "",
    val jenisThumbnail: String = "",
    var isChecked: Boolean = false,
    var value: String = ""
)

typealias ListJenisHp = List<JenisHp>

fun ListJenisHp.toRequest() = this.map { it.toRequest() }

fun JenisHp.toRequest() = JenisHpRequest(id = this.id, jenisHpId = this.jenisId)