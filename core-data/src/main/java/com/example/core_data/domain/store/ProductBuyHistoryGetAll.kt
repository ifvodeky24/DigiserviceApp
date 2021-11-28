package com.example.core_data.domain.store

data class ProductBuyHistoryGetAll(
    val jualId: Int = 0,
    val beliPembeli: Int = 0,
    val jualHarga: Int = 0,
    val jualJudul: String = "",
    val fotoProduk: String = "",
    val jualUserId: Int = 0,
    val beliJualId: Int = 0,
    val beliTglBeli: String = "",
    val id: Int = 0,
    val teknisiLat: String = "",
    val beliJasaKurir: String = "",
    val beliId: Int = 0,
    val teknisiId: Int = 0,
    val pelangganId: Int = 0,
    val beliTglBooking: String = "",
    val teknisiNama: String = "",
    val pelangganNama: String = "",
    val beliStatus: String = "",
)

typealias ListProductBuyHistoryGetAll = List<ProductBuyHistoryGetAll>