package com.example.gagyeboost.ui.home.calendar

import java.util.*

class CustomCalendar {

    private val calendar = Calendar.getInstance()

    var prevMonthTailOffset = 0
    var currentMonthMaxDate = 0

    private val _datesInMonth = mutableListOf<Int>()
    val datesInMonth: List<Int> = _datesInMonth

    fun setYearAndMonth(year: Int, month: Int) {
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month - 1)
        makeMonthDate(calendar)
    }

    private fun makeMonthDate(calendar: Calendar) {
        _datesInMonth.clear()
        calendar.set(Calendar.DATE, 1)

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DATE)
        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1

        makePrevMonthTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)
    }

    private fun makePrevMonthTail(calendar: Calendar) {
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1)

        repeat(prevMonthTailOffset) {
            _datesInMonth.add(-1)
        }
    }

    private fun makeCurrentMonth(calendar: Calendar) {
        repeat(calendar.getActualMaximum(Calendar.DATE)) {
            _datesInMonth.add(it + 1)
        }
    }

    companion object {
        const val DAYS_OF_WEEK = 7
    }
}
