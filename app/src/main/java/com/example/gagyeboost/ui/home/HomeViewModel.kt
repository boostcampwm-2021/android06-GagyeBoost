package com.example.gagyeboost.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.gagyeboost.model.Repository
import java.util.*

class HomeViewModel(val repository: Repository) : ViewModel() {

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _yearMonthPair = MutableLiveData<Pair<Int, Int>>()

    private val calendar = CustomCalendar()

    val dateItemList = Transformations.map(_yearMonthPair) {
        tempSetDataItemList()
    }

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

        calendar.setYearAndMonth(year, month)
        _yearAndMonth.value = stringDate
        _yearMonthPair.value = Pair(year, month)
    }

    private fun tempSetDataItemList(): MutableList<DateItem> {
        // TODO repository에서 가져온 데이터 가공
        val list = mutableListOf<DateItem>()
        calendar.datesInMonth.forEachIndexed { index, it ->
            list.add(
                DateItem(
                    null,
                    (0..100000).random(),
                    it,
                    2021,
                    11,
                    setDateColor(index),
                    setDateAlpha(index)
                )
            )
        }
        return list
    }

    private fun setDateColor(position: Int): String =
        when (position % CustomCalendar.DAYS_OF_WEEK) {
            0 -> "#D96D84"
            6 -> "#6195e6"
            else -> "#676d6e"
        }


    private fun setDateAlpha(position: Int): Float {
        val alpha = if (position < calendar.prevMonthTailOffset
            || position >= calendar.prevMonthTailOffset + calendar.currentMonthMaxDate
        ) {
            0.3f
        } else {
            1f
        }
        return alpha
    }

}
