package com.example.gagyeboost.model.data

data class DateItem(
    val expense: Int?,
    val income: Int?,
    val date: Int,
    val year: Int,
    val month: Int,
    val color: String
)

data class Filter(
    val moneyType: Byte?,
    val startYear: Int,
    val startMonth: Int,
    val startDay: Int,
    val endYear: Int,
    val endMonth: Int,
    val endDay: Int,
    val startLatitude: Float,
    val startLongitude: Float,
    val endLatitude: Float,
    val endLongitude: Float,
    val startMoney: Int,
    val endMoney: Int,
    val categoryList: List<Int>
)
