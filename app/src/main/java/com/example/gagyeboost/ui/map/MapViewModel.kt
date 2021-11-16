package com.example.gagyeboost.ui.map

import android.util.Log
import androidx.lifecycle.*
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.DateDetailItem
import com.example.gagyeboost.model.data.Filter
import kotlinx.coroutines.launch
import java.util.*

class MapViewModel(private val repository: Repository) : ViewModel() {

    val byteMoneyType = MutableLiveData(EXPENSE)
    val intMoneyType: LiveData<Int> = Transformations.map(byteMoneyType) { it.toInt() }

    val intStartMoney = MutableLiveData(0)
    val startMoney: LiveData<String> =
        Transformations.map(intStartMoney) { formatter.format(it) + "원" }

    val intEndMoney = MutableLiveData(300000)
    val endMoney: LiveData<String> = Transformations.map(intEndMoney) {
        if (it == Int.MAX_VALUE) {
            formatter.format(1000000) + "원 이상"
        } else {
            formatter.format(it) + "원"
        }
    }
    // 필터로 보낼 id list
    val categoryIDList = MutableLiveData<MutableList<Int>>()
    // 화면에 보여줄 카테고리 리스트
    val categoryExpenseList = MutableLiveData<List<Category>>()
    val categoryIncomeList = MutableLiveData<List<Category>>()

    var isAllCategory = true

    var startYear: Int = 1900
    var startMonth: Int = 1
    var startDay: Int = 1
    var endYear: Int = 2500
    var endMonth: Int = 12
    var endDay: Int = 31
    var startLatitude: Float = 0.0F
    var startLongitude: Float = 00.0F
    var endLatitude: Float = 200.0F
    var endLongitude: Float = 200.0F

    private val filterData = MutableLiveData<List<AccountBook>>()

    // HashMap<좌표, 좌표에 해당하는 내역 list>
    val dataMap: LiveData<HashMap<Pair<Float, Float>, List<AccountBook>>> =
        Transformations.map(filterData) { listToHashMap(it) }

    private var _selectedDetailList = MutableLiveData<List<DateDetailItem>>()
    val selectedDetailList: LiveData<List<DateDetailItem>> = _selectedDetailList

    fun setSelectedDetail(latitude: Float, longitude: Float) {
        Log.e("setSelectedDetail", "확인")
        val dataList = dataMap.value?.getOrPut(Pair(latitude, longitude)) { listOf() }
        _selectedDetailList.value = (dataList ?: listOf()).map {
            DateDetailItem(
                it.id,
                "\uD83E\uDD70",
                "밥",
                it.content,
                it.money.toString(),
                it.moneyType == INCOME
            )
        }
    }

    private fun listToHashMap(dataList: List<AccountBook>): HashMap<Pair<Float, Float>, List<AccountBook>> {
        val nowMap = HashMap<Pair<Float, Float>, MutableList<AccountBook>>()
        dataList.forEach {
            val latLng = Pair(it.latitude, it.longitude)
            nowMap.getOrPut(latLng) { mutableListOf() }.add(it)
        }
        val retMap = HashMap<Pair<Float, Float>, List<AccountBook>>()
        nowMap.forEach {
            retMap[it.key] = it.value
        }
        return retMap
    }

    fun hashMapToMarkerMap(dataMap: HashMap<Pair<Float, Float>, List<AccountBook>>): HashMap<Pair<Float, Float>, Pair<String, String>> {
        val markerMap = HashMap<Pair<Float, Float>, Pair<String, String>>()
        dataMap.forEach {
            markerMap[it.key] = Pair(
                it.value[0].address,
                "${it.value.sumOf { accountBook -> accountBook.money }}원"
            )
        }
        return markerMap
    }

    fun setInitData() {
        byteMoneyType.value = EXPENSE
        intStartMoney.value = 0
        intEndMoney.value = 300000
        startYear = Calendar.getInstance().get(Calendar.YEAR)
        startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        startDay = 1
        endYear = startYear
        endMonth = startMonth
        endDay = Calendar.getInstance().getActualMaximum(Calendar.DATE)
        // TODO 화면에 보이는 위도/경도로 설정 해야함
        startLatitude = 0.0f
        startLongitude = 0.0f
        endLatitude = 200.0f
        endLongitude = 200.0f
        initLoadCategory()
    }

    private fun initLoadCategory() {
        viewModelScope.launch {
            val expenseCategory = repository.loadCategoryList(EXPENSE)
            val incomeCategory = repository.loadCategoryList(INCOME)
            categoryExpenseList.postValue(expenseCategory)
            categoryIncomeList.postValue(incomeCategory)
            categoryIDList.postValue(expenseCategory.map { it.id }.toMutableList())
        }
    }

    fun setCategoryIDList(moneyType: Byte) {
        when (moneyType) {
            INCOME -> categoryIDList.value = categoryIncomeList.value?.map { it.id }?.toMutableList()
            EXPENSE -> categoryIDList.value = categoryExpenseList.value?.map { it.id }?.toMutableList()
        }
    }

    fun getCategoryList(): List<Category> {
        return when (byteMoneyType.value) {
            INCOME -> categoryIncomeList.value ?: listOf()
            else -> categoryExpenseList.value ?: listOf()
        }
    }

    fun setMoney(start: Int, end: Int) {
        intStartMoney.value = start
        intEndMoney.value = end
    }

    fun loadFilterData() {
        viewModelScope.launch {
            val data = setFilter()
            val filter = repository.loadFilterData(data)
            filterData.postValue(filter)
        }
    }

    private fun setFilter() = Filter(
        byteMoneyType.value ?: EXPENSE,
        startYear,
        startMonth,
        startDay,
        endYear,
        endMonth,
        endDay,
        startLatitude,
        startLongitude,
        endLatitude,
        endLongitude,
        intStartMoney.value ?: 0,
        intEndMoney.value ?: 300000,
        categoryIDList.value ?: listOf()
    )

    fun setPeriod(startDate: Date, endDate: Date) {
        val calendar = Calendar.getInstance()
        calendar.time = startDate
        startYear = calendar.get(Calendar.YEAR)
        startMonth = calendar.get(Calendar.MONTH) + 1
        startDay = calendar.get(Calendar.DAY_OF_MONTH)
        calendar.time = endDate
        endYear = calendar.get(Calendar.YEAR)
        endMonth = calendar.get(Calendar.MONTH) + 1
        endDay = calendar.get(Calendar.DAY_OF_MONTH)
    }
}
