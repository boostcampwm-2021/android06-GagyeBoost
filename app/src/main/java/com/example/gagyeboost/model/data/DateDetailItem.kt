package com.example.gagyeboost.model.data

import java.io.Serializable

data class DateDetailItem(
    val id: String,
    val emoji: String,
    val category: String,
    val content: String,
    val money: String,
    val moneyType: Boolean,
    val address: String
) : Serializable
