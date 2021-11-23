package com.example.gagyeboost.common

import java.text.DecimalFormat
import java.util.*

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

// StatisticsFragment, StatisticsViewModel
const val ANIMATE_Y_TIME = 1500
const val CHART_Y_AXIS_UNIT = 10000
const val MAX_LIST_ITEMS = 5

// INTENT
const val INTENT_EXTRA_PLACE_DETAIL = "INTENT_EXTRA_PLACE_DETAIL"

val NOW_YEAR = Calendar.getInstance().get(Calendar.YEAR)
val NOW_MONTH = Calendar.getInstance().get(Calendar.MONTH) + 1
val END_DAY = Calendar.getInstance().getActualMaximum(Calendar.DATE)

// SearchFragment
const val DEFAULT_START_YEAR = 1970
const val DEFAULT_END_YEAR = 2500
