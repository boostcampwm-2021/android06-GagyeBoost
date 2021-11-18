package com.example.gagyeboost.model.data

data class DateDetailItem(
    val id: Int,
    var emoji: String,
    var category: String,
    var content: String,
    var money: Int,
    val moneyType: Boolean,
)
