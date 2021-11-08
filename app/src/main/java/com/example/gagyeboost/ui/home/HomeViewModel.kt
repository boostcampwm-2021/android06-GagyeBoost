package com.example.gagyeboost.ui.home

import androidx.lifecycle.*
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
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

    fun afterMoneyTextChanged() {
        if (money.value.isNullOrEmpty()) money.value = "0"

        money.value = money.value?.replaceFirst("^0+(?!$)".toRegex(), "");
    }

    fun getFormattedMoneyText(money: Int) = formatter.format(money) + "원"

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
                        getFormattedMoneyText(account.money),
                        account.moneyType == INCOME
                    )
                )
            }

            data.postValue(list)
        }

        return data
    }
}
