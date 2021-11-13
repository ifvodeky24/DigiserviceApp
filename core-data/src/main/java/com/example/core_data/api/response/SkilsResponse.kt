package com.example.core_data.api.response

import com.example.core_data.domain.*
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
internal data class SkilsResponse(
    val code: Int,
    val message: String,
    val status: String,
    val result: ResultSkilsResponse,
)


@JsonClass(generateAdapter = true)
internal data class ResultSkilsResponse(
    @Json(name = "jenis_kerusakan")
    val skils: ListSkilsResponse,
    @Json(name = "jenis_hp")
    val jenisHp: ListJenisHpResponse,
)

internal fun ResultSkilsResponse.toDomain() = ResultSkils(
    skils = skils.toDomain(),
    jenisHp = jenisHp.toDomain()
)

@JsonClass(generateAdapter = true)
internal data class DataSkilsResponse(
    @Json(name = "id")
    val id: Int,
    @Json(name = "teknisi_kerusakan_jenis_hp_id")
    val teknisiKerusakanJenisHpId: Int,
    @Json(name = "jenis_kerusakan_hp_id")
    val jenisKerusakanHpId: Int,
    @Json(name = "teknisi_nama")
    val teknisiNama: String,
    @Json(name = "nama_kerusakan")
    val namKerusakan: String
)

internal typealias ListSkilsResponse = List<DataSkilsResponse>

internal fun ListSkilsResponse.toDomain() = map {
    it.toDomain()
}

internal fun DataSkilsResponse.toDomain() = Skils(
    id = id,
    teknisiKerusakanJenisHpId = teknisiKerusakanJenisHpId,
    jenisKerusakanHpId = jenisKerusakanHpId,
    teknisiNama = teknisiNama,
    namaKerusakan = namKerusakan,
)