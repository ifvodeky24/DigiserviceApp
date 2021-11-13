package com.example.core_data.domain

import com.example.core_data.api.request.JenisKerusakanRequest

data class JenisKerusakan(
    val idKerusakan: Int = 0,
    val namaKerusakan: String = "",
    val deskripsiKerusakan: String = "",
    val isChecked: Boolean = false,
    val value: String = ""
)

typealias ListJenisKerusakan = List<JenisKerusakan>

fun ListJenisKerusakan.toRequest() = this.map { it.toRequest() }

fun JenisKerusakan.toRequest() = JenisKerusakanRequest(kerusakanJenisHpId = this.idKerusakan)