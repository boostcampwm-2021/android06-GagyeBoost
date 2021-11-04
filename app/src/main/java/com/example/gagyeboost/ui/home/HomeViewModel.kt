package com.example.gagyeboost.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _yearMonthPair = MutableLiveData<Pair<Int, Int>>()
    val yearMonthPair: LiveData<Pair<Int, Int>> = _yearMonthPair

    private val _dateItemList = MutableLiveData<List<DateItem>>()
    val dateItemList: LiveData<List<DateItem>> = _dateItemList

    private val calendar = CustomCalendar()

    val selectedDate = MutableLiveData<DateItem>()

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1)
        loadAllDayDataInMonth()
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

        calendar.setYearAndMonth(year, month)
        _yearAndMonth.value = stringDate
        _yearMonthPair.value = Pair(year, month)
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

    fun loadAllDayDataInMonth() {
        viewModelScope.launch {
            val dateItems = mutableListOf<DateItem>()
            calendar.datesInMonth.forEachIndexed { index, date ->
                val accountDataList =
                    repository.loadDayData(
                        _yearMonthPair.value?.first ?: 0,
                        _yearMonthPair.value?.second ?: 0,
                        date
                    )

                var totalExpense = 0
                var totalIncome = 0

                accountDataList.forEach { record ->
                    when (record.moneyType) {
                        0.toByte() -> totalExpense += record.money
                        1.toByte() -> totalIncome += record.money
                    }
                }

                dateItems.add(
                    DateItem(
                        if (totalExpense == 0) null else totalExpense,
                        if (totalIncome == 0) null else totalIncome,
                        date,
                        _yearMonthPair.value?.first ?: 0,
                        _yearMonthPair.value?.second ?: 0,
                        setDateColor(index),
                        setDateAlpha(index)
                    )
                )
            }
            _dateItemList.postValue(dateItems)
        }
    }

    fun getTodayString() = selectedDate.value?.let {
        it.year.toString() + "/" + it.month + "/" + it.date
    } ?: ""

}
