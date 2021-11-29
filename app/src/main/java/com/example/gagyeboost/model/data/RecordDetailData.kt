package com.example.gagyeboost.model.data

import androidx.room.ColumnInfo

data class RecordDetailData(
    val id: Int = 0,
    @ColumnInfo(name = "money_type")
    val moneyType: Int,
    var money: Int,
    @ColumnInfo(name = "category")
    var categoryID: Int,
    var latitude: Double,
    var longitude: Double,
    var address: String,
    var content: String,
    var year: Int,
    var month: Int,
    var day: Int,
    @ColumnInfo(name = "category_name")
    var categoryName: String,
    var emoji: String
)
