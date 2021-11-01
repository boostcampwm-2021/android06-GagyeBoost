package com.example.gagyeboost.ui.home

import java.util.*

class CustomCalendar {

    private val calendar = Calendar.getInstance()

    var prevMonthTailOffset = 0
    var nextMonthHeadOffset = 0
    var currentMonthMaxDate = 0

    private val _datesInMonth = mutableListOf<Int>()
    val datesInMonth get() = _datesInMonth

    init {
        calendar.time = Date()
        initCalendar { }
    }

    private fun initCalendar(refreshCallback: (Calendar) -> Unit) {
        makeMonthDate(refreshCallback)
    }

    private fun makeMonthDate(refreshCallback: (Calendar) -> Unit) {
        _datesInMonth.clear()

        calendar.set(Calendar.DATE, 1)

        currentMonthMaxDate = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        prevMonthTailOffset = calendar.get(Calendar.DAY_OF_WEEK) - 1

        makePrevMonthTail(calendar.clone() as Calendar)
        makeCurrentMonth(calendar)

        nextMonthHeadOffset =
            LOW_OF_CALENDAR * DAYS_OF_WEEK - (prevMonthTailOffset + currentMonthMaxDate)
        makeNextMonthHead()

        refreshCallback(calendar)
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