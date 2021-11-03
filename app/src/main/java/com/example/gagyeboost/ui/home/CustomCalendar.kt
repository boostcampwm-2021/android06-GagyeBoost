package com.example.gagyeboost.ui.home

import android.util.Log
import java.util.*

class CustomCalendar {

    private val calendar = Calendar.getInstance()

    var prevMonthTailOffset = 0
    var nextMonthHeadOffset = 0
    var currentMonthMaxDate = 0

    private val _datesInMonth = mutableListOf<Int>()
    val datesInMonth get() = _datesInMonth

    init {
        makeMonthDate(calendar)
    }

    fun setYearAndMonth(year: Int, month: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        Log.e("calendar","set year: $year , set month: $month")
        makeMonthDate(calendar)
    }

    private fun makeMonthDate(calendar: Calendar) {
        _datesInMonth.clear()

        calendar.set(Calendar.DATE, 1)

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DATE)
        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1

        Log.e("calendar","Month : ${calendar.get(Calendar.MONTH) +1}")
        Log.e("calendar","currentMonthMaxDate : $currentMonthMaxDate")
        makePrevMonthTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)

        nextMonthHeadOffset =
            LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate)
        makeNextMonthHead()

    }

    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)
        val maxDate = calendar.getActualMaximum(Calendar.DATE)
        var maxOffsetDate = maxDate - prevMonthTailOffset

        repeat(prevMonthTailOffset) {
            _datesInMonth.add(++maxOffsetDate)
        }
    }

    private fun makeCurrentMonth(calendar: Calendar) {
        repeat(calendar.getActualMaximum(Calendar.DATE)) {
            _datesInMonth.add(it + 1)
        }
    }

    private fun makeNextMonthHead() {
        var date = 1

        repeat(nextMonthHeadOffset) {
            _datesInMonth.add(date++)
        }
    }

    companion object {
        const val DAYS_OF_WEEK = 7
        const val LOW_OF_CALENDAR = 6
    }
}