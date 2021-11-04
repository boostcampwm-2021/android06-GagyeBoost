package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.DateAlpha
import com.example.gagyeboost.model.data.DateColor
import com.example.gagyeboost.model.data.DateItem
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _yearMonthPair = MutableLiveData<Pair<Int, Int>>()

    private val calendar = CustomCalendar()

    val dateItemList = Transformations.map(_yearMonthPair) {
        tempSetDataItemList()
    }

    val selectedDate = MutableLiveData<DateItem>()

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
            0 -> DateColor.Sunday.color
            6 -> DateColor.Saturday.color
            else -> DateColor.Weekday.color
        }

    private fun setDateAlpha(position: Int): Float {
        val alpha = if (position < calendar.prevMonthTailOffset
            || position >= calendar.prevMonthTailOffset + calendar.currentMonthMaxDate
        ) {
            DateAlpha.Percent30.alpha
        } else {
            DateAlpha.Percent100.alpha
        }
        return alpha
    }

    fun loadAllDayDataInMonth() {
        viewModelScope.launch {
            val dateItemList = calendar.datesInMonth.map { date ->
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
                // TODO DateItem 작성
//                DateItem(totalExpense, totalIncome, date, _yearMonthPair.value?.first ?: 0, _yearMonthPair.value?.second ?: 0, setDateColor())
            }
        }
    }

    fun getTodayString() = selectedDate.value?.let {
        it.year.toString() + "/" + it.month + "/" + it.date
    } ?: ""

}
