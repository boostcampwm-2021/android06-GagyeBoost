package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.*
import kotlinx.coroutines.launch
import java.text.DecimalFormat
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

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    val selectedDate = MutableLiveData<DateItem>()

    val detailItemList = Transformations.switchMap(selectedDate) {
        loadDateDetailItemList(it)
    }

    val money = MutableLiveData<String>("0")

    private val formatter = DecimalFormat("###,###")

    private val _totalMonthIncome = MutableLiveData<String>()
    val totalMonthIncome: LiveData<String> = _totalMonthIncome

    private val _totalMonthExpense = MutableLiveData<String>()
    val totalMonthExpense: LiveData<String> = _totalMonthExpense

    private val _totalMonthBalance = MutableLiveData<String>()
    val totalMonthBalance: LiveData<String> = _totalMonthBalance

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
            val dateItems = mutableListOf<DateItem>()
            var monthIncome = 0
            var monthExpense = 0

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

                monthIncome += totalIncome
                monthExpense += totalExpense
            }
            _dateItemList.postValue(dateItems)
            _totalMonthIncome.postValue(formatter.format(monthIncome) + "원")
            _totalMonthExpense.postValue(formatter.format(monthExpense) + "원")
            _totalMonthBalance.postValue(formatter.format(monthIncome - monthExpense) + "원")
        }
    }

    fun getTodayString() = selectedDate.value?.let {
        it.year.toString() + "/" + it.month + "/" + it.date
    } ?: ""

    fun loadDateDetailItemList(date: DateItem): LiveData<MutableList<DateDetailItem>> {
        val data = MutableLiveData<MutableList<DateDetailItem>>()

        val list = mutableListOf<DateDetailItem>()

        viewModelScope.launch {
            repository.loadDayData(date.year, date.month, date.date).forEach { account ->
                val category = repository.loadCategoryData(account.category)
                list.add(
                    DateDetailItem(
                        account.id.toString(),
                        category.emoji,
                        category.categoryName,
                        account.content,
                        formatter.format(account.money) + "원",
                        account.moneyType == 1.toByte()
                    )
                )
            }

            data.postValue(list)
        }

        return data
    }
}
