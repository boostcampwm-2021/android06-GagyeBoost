package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.common.NOW_YEAR
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.DateColor
import com.example.gagyeboost.model.data.DateItem
import com.example.gagyeboost.model.data.MonthTotalMoney
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _yearMonthPair = MutableLiveData<Pair<Int, Int>>()
    val yearMonthPair: LiveData<Pair<Int, Int>> = _yearMonthPair

    private val _dateItemList = MutableLiveData<List<DateItem>>()
    val dateItemList: LiveData<List<DateItem>> = _dateItemList

    private val calendar = CustomCalendar()

    private val _selectedDate = MutableLiveData<DateItem?>()
    val selectedDate: LiveData<DateItem?> = _selectedDate

    val monthTotalMoney = MonthTotalMoney(MutableLiveData(), MutableLiveData(), MutableLiveData())

    val detailItemList = Transformations.switchMap(_selectedDate) { date ->
        if (date == null) {
            MutableLiveData(listOf())
        } else {
            repository.flowLoadDayData(date.year, date.month, date.date).asLiveData()
        }
    }

    init {
        setYearAndMonth(NOW_YEAR, Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == NOW_YEAR) "${month}월" else "${year}년 ${month}월"

        calendar.setYearAndMonth(year, month)
        _yearAndMonth.value = stringDate
        _yearMonthPair.value = Pair(year, month)
        _selectedDate.value = null
    }

    private fun setDateColor(position: Int): String =
        when (position % CustomCalendar.DAYS_OF_WEEK) {
            0 -> DateColor.Sunday.color
            6 -> DateColor.Saturday.color
            else -> DateColor.Weekday.color
        }

    fun setSelectedDate(dateItem: DateItem) {
        _selectedDate.value = dateItem
    }

    fun loadAllDayDataInMonth() {
        viewModelScope.launch {
            val dateItems = mutableListOf<DateItem>()
            calendar.datesInMonth.forEachIndexed { index, date ->
                // prev month date
                if (date < 0) {
                    dateItems.add(DateItem(null, null, date, 0, 0, setDateColor(index)))
                    return@forEachIndexed
                }
                val total = repository.loadDayTotalMoney(
                    _yearMonthPair.value?.first ?: 0,
                    _yearMonthPair.value?.second ?: 0,
                    date
                )

                dateItems.add(
                    DateItem(
                        total?.expenseMoney,
                        total?.incomeMoney,
                        date,
                        _yearMonthPair.value?.first ?: 0,
                        _yearMonthPair.value?.second ?: 0,
                        setDateColor(index)
                    )
                )
            }
            val monthIncome = dateItems.sumOf { it.income ?: 0 }
            val monthExpense = dateItems.sumOf { it.expense ?: 0 }
            monthTotalMoney.totalIncome.value = monthIncome
            monthTotalMoney.totalExpense.value = monthExpense
            monthTotalMoney.totalBalance.value = monthIncome - monthExpense
            _dateItemList.postValue(dateItems)
        }
    }

    fun getTodayString() = _selectedDate.value?.let {
        it.year.toString() + "/" + it.month + "/" + it.date
    } ?: ""
}
