package com.example.gagyeboost.common

import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
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
// StatisticsFragment, StatisticsViewModel
const val ANIMATE_Y_TIME = 1500
const val CHART_Y_AXIS_UNIT = 10000
const val MAX_LIST_ITEMS = 5

// INTENT
const val INTENT_EXTRA_PLACE_DETAIL = "INTENT_EXTRA_PLACE_DETAIL"


fun TextInputEditText.setEditTextSize(textView: TextView) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            textView.text = s.toString()
            val size = textView.textSize / 3
            textSize = size
        }

        override fun afterTextChanged(s: Editable?) {}

    })
}