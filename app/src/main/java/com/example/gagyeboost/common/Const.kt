package com.example.gagyeboost.common

import java.text.DecimalFormat

// AddFragment, CategoryFragment
const val IS_EXPENSE_KEY = "isExpense"
const val TODAY_STRING_KEY = "today"

// Money Type
const val INCOME = 1.toByte()
const val EXPENSE = 0.toByte()

// monney format to won
val formatter = DecimalFormat("###,###")