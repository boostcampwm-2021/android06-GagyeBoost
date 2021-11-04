package com.example.gagyeboost.ui.home

import android.util.Log
import androidx.lifecycle.*
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.DateDetailItem
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.DecimalFormat
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

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    val selectedDate = MutableLiveData<DateItem>()

    val detailItemList = Transformations.switchMap(selectedDate) {
        getDateDetailItemList(it)
    }

    val money = MutableLiveData<String>("0")

    private val formatter = DecimalFormat("###,###")

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

    fun afterMoneyTextChanged() {
        if (money.value.isNullOrEmpty()) money.value = "0"

        money.value = money.value?.replaceFirst("^0+(?!$)".toRegex(), "");
    }

    fun loadCategoryList() {
        viewModelScope.launch {
            _categoryList.value = repository.loadCategoryList()
            Log.d("TAG", _categoryList.value.toString())
        }
    }

    fun getFormattedMoneyText(money: Int) = formatter.format(money) + "원"

    fun getDateDetailItemList(date: DateItem): LiveData<MutableList<DateDetailItem>> {
        val data = MutableLiveData<MutableList<DateDetailItem>>()

        val list = mutableListOf<DateDetailItem>()

        viewModelScope.launch {
            val categoryList = repository.loadCategoryList()

            repository.loadDayData(date.year, date.month, date.date).forEach { account ->
                val category = categoryList.find { category ->
                    category.id == account.category
                }
                list.add(
                    DateDetailItem(
                        account.id.toString(),
                        category?.emoji ?: "NO",
                        category?.categoryName ?: "NO",
                        account.content,
                        getFormattedMoneyText(account.money)
                    )
                )
            }

            data.postValue(list)
        }

        return data
    }
}

