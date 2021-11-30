package com.example.gagyeboost.model.data

import androidx.room.ColumnInfo

data class DateDetailItem(
    val id: Int,
    var emoji: String,
    @ColumnInfo(name = "category_name")
    var category: String,
    var content: String,
    var money: Int,
    @ColumnInfo(name = "money_type")
    val moneyType: Boolean,
)
