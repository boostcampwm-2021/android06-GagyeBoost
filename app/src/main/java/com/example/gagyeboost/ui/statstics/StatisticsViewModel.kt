package com.example.gagyeboost.ui.statstics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.CHART_Y_AXIS_UNIT
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.StatRecordItem
import com.example.gagyeboost.model.data.nothingEmoji
import com.example.gagyeboost.ui.home.CustomCalendar
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.round

class StatisticsViewModel(private val repository: Repository) : ViewModel() {
    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _yearMonthPair = MutableLiveData<Pair<Int, Int>>()
    val yearMonthPair: LiveData<Pair<Int, Int>> = _yearMonthPair

    private val calendar = CustomCalendar()

    private val _selectedMoneyType = MutableLiveData(EXPENSE)
    val selectedMoneyType: LiveData<Byte> = _selectedMoneyType

    private val _dailyChartData = MutableLiveData<List<Pair<Int, Int>>>()
    val dailyChartData: LiveData<List<Pair<Int, Int>>> = _dailyChartData

    private val _sortedStatRecordList = MutableLiveData<List<StatRecordItem>>()
    val sortedStatRecordList: LiveData<List<StatRecordItem>> = _sortedStatRecordList

    private val _totalMoneyAmount = MutableLiveData<String>()
    val totalMoneyAmount: LiveData<String> = _totalMoneyAmount

    private val _isShowingAllData = MutableLiveData(false)
    val isShowingAllData: LiveData<Boolean> = _isShowingAllData

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH) + 1)
        setDailyChartData()
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

        calendar.setYearAndMonth(year, month)
        _yearAndMonth.value = stringDate
        _yearMonthPair.value = Pair(year, month)
    }

    fun setDailyChartData() {
        var totalSum = 0
        viewModelScope.launch {
            val dataList = mutableListOf<Pair<Int, Int>>()
            calendar.datesInMonth.forEach { date ->
                // include only current month date
                if (date < 0) return@forEach

                val accountDataList =
                    repository.loadDayData(
                        _yearMonthPair.value?.first ?: 0,
                        _yearMonthPair.value?.second ?: 0,
                        date
                    ).filter { it.moneyType == _selectedMoneyType.value }

                val totalMoney =
                    accountDataList.fold(0) {
                            total, record: AccountBook -> total + record.money
                    } / CHART_Y_AXIS_UNIT
                totalSum += totalMoney * CHART_Y_AXIS_UNIT

                if (totalMoney > 0) dataList.add(Pair(date, totalMoney))
            }
            _dailyChartData.value = dataList
            _totalMoneyAmount.value = formatter.format(totalSum)
        }
    }

    fun setSelectedMoneyType(type: Byte) {
        _selectedMoneyType.value = type
    }

    fun loadRecordList() {
        viewModelScope.launch(IO) {
            val type = selectedMoneyType.value ?: 0
            val deferredCategory =
                async { repository.loadCategoryList(type).map { Pair(it.id, it) }.toMap() }
            val timePair = yearMonthPair.value ?: Pair(
                currentYear,
                Calendar.getInstance().get(Calendar.MONTH) + 1
            )
            val deferredMonthData =
                async { repository.loadMonthData(timePair.first, timePair.second) }
            val recordSumMap = HashMap<Int, Int>()
            deferredMonthData.await().filter {
                it.moneyType == type
            }.forEach {
                recordSumMap[it.category] = (recordSumMap[it.category] ?: 0) + it.money
            }

            val categoryMap = deferredCategory.await()
            val sum = recordSumMap.toList().sumOf { it.second }
            _sortedStatRecordList.postValue(
                recordSumMap.toList().sortedWith { a: Pair<Int, Int>, b: Pair<Int, Int> ->
                    when {
                        a.second < b.second -> 1
                        a.second > b.second -> -1
                        else -> 0
                    }
                }.map {
                    val category = categoryMap[it.first] ?: Category(0, "", nothingEmoji, type)
                    StatRecordItem(
                        category.id,
                        category.emoji,
                        category.categoryName,
                        round(it.second.toDouble() * 100 / sum).toInt(),
                        formatter.format(it.second)
                    )
                })
        }
    }

    fun setDataListState() {
        val isShowingAll = _isShowingAllData.value ?: false
        _isShowingAllData.value = !isShowingAll
    }
}
