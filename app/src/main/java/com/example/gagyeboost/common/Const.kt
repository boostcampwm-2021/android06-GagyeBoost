package com.example.gagyeboost.common

import com.example.gagyeboost.model.data.Filter
import java.text.DecimalFormat

// AddFragment, CategoryFragment
const val IS_EXPENSE_KEY = "isExpense"
const val TODAY_STRING_KEY = "today"

// Money Type
const val INCOME = 1.toByte()
const val EXPENSE = 0.toByte()

// HomeFragment, RecordDetailActivity
const val DATE_DETAIL_ITEM_ID_KEY = "dateDetailItem"

// money format to won
val formatter = DecimalFormat("###,###")

// GPSUtils
const val DEFAULT_LAT = 37.49724110935863
const val DEFAULT_LNG = 127.02877164249468

// filter default
val DEFAULT_FILTER = Filter(
    EXPENSE, 1900,
    1,
    1,
    2500,
    12,
    31,
    0.0F,
    00.0F,
    200.0F,
    200.0F,
    0,
    1000000,
    listOf()
)