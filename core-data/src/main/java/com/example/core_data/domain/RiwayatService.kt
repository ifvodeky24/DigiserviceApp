package com.example.core_data.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RiwayatService(
    val nama: String,
    val merek: String,
    val kerusakan: String,
    val deskripsi: String
) : Parcelable
