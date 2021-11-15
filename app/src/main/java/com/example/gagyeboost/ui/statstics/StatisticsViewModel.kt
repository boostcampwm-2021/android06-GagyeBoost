package com.example.gagyeboost.ui.statstics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.ui.home.CustomCalendar
import java.util.*

class StatisticsViewModel(private val repository: Repository): ViewModel() {
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _yearMonthPair = MutableLiveData<Pair<Int, Int>>()
    val yearMonthPair: LiveData<Pair<Int, Int>> = _yearMonthPair

    private val calendar = CustomCalendar()

    private val _selectedMoneyType = MutableLiveData(EXPENSE)
    val selectedMoneyType: LiveData<Byte> = _selectedMoneyType

    private val _dailyChartData = MutableLiveData<MutableMap<String, Int>>()
    val dailyChartData: LiveData<MutableMap<String, Int>> = _dailyChartData

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1)
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

        calendar.setYearAndMonth(year, month)
        _yearAndMonth.value = stringDate
        _yearMonthPair.value = Pair(year, month)
    }

    fun setDailyChartData() {

    }

    fun setSelectedMoneyType(type: Byte) {
        _selectedMoneyType.value = type
    }

    private fun dateStringFormatter(date: Int) = "" + _yearMonthPair.value?.second + "." + date
}
