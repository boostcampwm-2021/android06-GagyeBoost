package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.common.INIT_POSITION
import com.example.gagyeboost.common.NOW_MONTH
import com.example.gagyeboost.common.NOW_YEAR
import com.example.gagyeboost.common.intToStringDate
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.CustomDate
import com.example.gagyeboost.model.data.MonthTotalMoney
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: Repository) : ViewModel() {

    private var nowYearMonth = CustomDate(2021, 11, 0)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _selectedDate = MutableLiveData<CustomDate?>()
    val selectedDate: LiveData<CustomDate?> = _selectedDate

    val monthTotalMoney = MonthTotalMoney(MutableLiveData(), MutableLiveData(), MutableLiveData())

    var viewPagerPosition = INIT_POSITION

    val detailItemList = Transformations.switchMap(_selectedDate) { date ->
        if (date == null) {
            MutableLiveData(listOf())
        } else {
            repository.flowLoadDayData(date.year, date.month, date.day).asLiveData()
        }
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == NOW_YEAR) "${month}월" else "${year}년 ${month}월"
        _yearAndMonth.value = stringDate
        nowYearMonth = CustomDate(year, month, 0)
        loadTotalMoney()
        checkSelectedDate(year, month)
    }

    private fun checkSelectedDate(year: Int, month: Int) {
        val data = _selectedDate.value ?: return
        if (data.year != year || data.month != month) {
            _selectedDate.value = null
        }
    }

    fun setSelectedDate(item: CustomDate?) {
        _selectedDate.value = item
    }

    fun getTodayString() = _selectedDate.value?.let {
        intToStringDate(it.year, it.month, it.day)
    } ?: ""

    fun selectedMonthMinusNow(year: Int, month: Int): Int {
        val result = (year - NOW_YEAR) * 12 + (month - NOW_MONTH)
        viewPagerPosition = INIT_POSITION + result
        return result
    }

    fun loadTotalMoney() {
        viewModelScope.launch {
            _yearAndMonth.value?.let {
                val data =
                    repository.loadMonthExpenseAndIncome(nowYearMonth.year, nowYearMonth.month)
                monthTotalMoney.totalExpense.value = data.expenseMoney ?: 0
                monthTotalMoney.totalIncome.value = data.incomeMoney ?: 0
                monthTotalMoney.totalBalance.value =
                    (data.incomeMoney ?: 0) - (data.expenseMoney ?: 0)
            }
        }
    }
}
