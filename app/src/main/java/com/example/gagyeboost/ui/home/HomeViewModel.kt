package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.DateColor
import com.example.gagyeboost.model.data.DateItem
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

    private val _selectedDate = MutableLiveData<DateItem?>()
    val selectedDate: LiveData<DateItem?> = _selectedDate

    private val _totalMonthIncome = MutableLiveData<String>()
    val totalMonthIncome: LiveData<String> = _totalMonthIncome

    private val _totalMonthExpense = MutableLiveData<Int>()
    val totalMonthExpense: LiveData<Int> = _totalMonthExpense

    private val _totalMonthBalance = MutableLiveData<String>()
    val totalMonthBalance: LiveData<String> = _totalMonthBalance

    val detailItemList = Transformations.switchMap(_selectedDate) { date ->
        if (date == null) {
            MutableLiveData(listOf())
        } else {
            repository.flowLoadDayData(date.year, date.month, date.date).asLiveData()
        }
    }

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

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
            var monthIncome = 0
            var monthExpense = 0

            calendar.datesInMonth.forEachIndexed { index, date ->
                // prev month date
                if (date < 0) {
                    dateItems.add(
                        DateItem(null, null, date, 0, 0, setDateColor(index))
                    )
                    return@forEachIndexed
                }
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
                        EXPENSE -> totalExpense += record.money
                        INCOME -> totalIncome += record.money
                    }
                }

                dateItems.add(
                    DateItem(
                        if (totalExpense == 0) null else totalExpense,
                        if (totalIncome == 0) null else totalIncome,
                        date,
                        _yearMonthPair.value?.first ?: 0,
                        _yearMonthPair.value?.second ?: 0,
                        setDateColor(index)
                    )
                )

                monthIncome += totalIncome
                monthExpense += totalExpense
            }
            _dateItemList.postValue(dateItems)
            _totalMonthIncome.postValue(formatter.format(monthIncome) + "원")
            _totalMonthExpense.postValue(monthExpense)
            _totalMonthBalance.postValue(formatter.format(monthIncome - monthExpense) + "원")
        }
    }

    fun getTodayString() = _selectedDate.value?.let {
        it.year.toString() + "/" + it.month + "/" + it.date
    } ?: ""
}
