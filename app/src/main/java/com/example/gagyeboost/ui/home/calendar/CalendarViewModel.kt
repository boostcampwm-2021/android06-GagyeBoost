package com.example.gagyeboost.ui.home.calendar

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.DateColor
import com.example.gagyeboost.model.data.DateItem
import kotlinx.coroutines.launch

class CalendarViewModel(private val repository: Repository) : ViewModel() {

    private val calendar = CustomCalendar()

    private val _selectedDate = MutableLiveData<DateItem?>()
    val selectedDate: LiveData<DateItem?> = _selectedDate

    var selectedDatePosition: Int? = null

    private var _yearMonthPair = Pair(0, 0)

    private val _dateItemList = MutableLiveData<List<DateItem>>()
    val dateItemList: LiveData<List<DateItem>> = _dateItemList

    fun setYearAndMonth(year: Int, month: Int) {
        calendar.setYearAndMonth(year, month)
        _yearMonthPair = Pair(year, month)
        loadAllDayDataInMonth()
    }

    fun setSelectedDate(dateItem: DateItem?) {
        _selectedDate.value = dateItem
    }

    fun loadAllDayDataInMonth() {
        val year = _yearMonthPair.first
        val month = _yearMonthPair.second
        viewModelScope.launch {
            val dateItems = mutableListOf<DateItem>()
            calendar.datesInMonth.forEachIndexed { index, date ->
                // prev month date
                if (date < 0) {
                    dateItems.add(DateItem(null, null, date, 0, 0, setDateColor(index)))
                    return@forEachIndexed
                }
                val total = repository.loadDayTotalMoney(year, month, date)

                dateItems.add(
                    DateItem(
                        total?.expenseMoney,
                        total?.incomeMoney,
                        date,
                        year,
                        month,
                        setDateColor(index)
                    )
                )
            }
            _dateItemList.postValue(dateItems)
        }
    }

    private fun setDateColor(position: Int): String =
        when (position % CustomCalendar.DAYS_OF_WEEK) {
            0 -> DateColor.Sunday.color
            6 -> DateColor.Saturday.color
            else -> DateColor.Weekday.color
        }
}
