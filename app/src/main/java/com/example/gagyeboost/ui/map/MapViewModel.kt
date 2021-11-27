package com.example.gagyeboost.ui.map

import androidx.lifecycle.*
import com.example.gagyeboost.common.*
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.random.Random

class MapViewModel(private val repository: Repository) : ViewModel() {

    val byteMoneyType = MutableLiveData(EXPENSE)
    val intMoneyType: LiveData<Int> = Transformations.map(byteMoneyType) { it.toInt() }

    val intStartMoney = MutableLiveData(0)

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
    private val categoryExpenseList = MutableLiveData<List<Category>>()
    private val categoryIncomeList = MutableLiveData<List<Category>>()

    var startYear: Int = 1900
    var startMonth: Int = 1
    var startDay: Int = 1
    var endYear: Int = 2500
    var endMonth: Int = 12
    var endDay: Int = 31
    var startLatitude: Float = MIN_LAT.toFloat()
    var startLongitude: Float = MIN_LNG.toFloat()
    var endLatitude: Float = MAX_LAT.toFloat()
    var endLongitude: Float = MAX_LNG.toFloat()

    val isMoneyFilterChange = MutableLiveData(false)
    var moneyFilterBtnText = ""
    val isPeriodFilterChange = MutableLiveData(false)
    var periodFilterBtnText = ""
    val isCategoryBackgroundChange = MutableLiveData(false)

    // category filter adapter에서 필요한 초기 카테고리 리스트
    var incomeCategoryID: List<Int>? = null
    var expenseCategoryID: List<Int>? = null

    private val filterData = MutableLiveData<List<AccountBook>>()

    // HashMap<좌표, 좌표에 해당하는 내역 list>
    val dataMap: LiveData<HashMap<Pair<Double, Double>, List<AccountBook>>> =
        Transformations.map(filterData) { listToHashMap(it) }

    private var _selectedDetailList = MutableLiveData<List<DateDetailItem>>()
    val selectedDetailList: LiveData<List<DateDetailItem>> = _selectedDetailList

    fun setSelectedDetail(latitude: Double, longitude: Double) {
        viewModelScope.launch {
            val categoryMap = repository.loadCategoryMap()
            val dataList = dataMap.value?.getOrPut(Pair(latitude, longitude)) { listOf() }
            _selectedDetailList.value = (dataList ?: listOf()).map {
                val category = categoryMap[it.category] ?: Category(
                    it.category,
                    categoryName = "NULL",
                    emoji = nothingEmoji,
                    it.moneyType
                )
                DateDetailItem(
                    it.id,
                    category.emoji,
                    category.categoryName,
                    it.content,
                    it.money,
                    it.moneyType == INCOME
                )
            }
        }
    }

    private fun listToHashMap(dataList: List<AccountBook>): HashMap<Pair<Double, Double>, List<AccountBook>> {
        val nowMap = HashMap<Pair<Double, Double>, MutableList<AccountBook>>()
        dataList.forEach {
            val latLng = Pair(it.latitude, it.longitude)
            nowMap.getOrPut(latLng) { mutableListOf() }.add(it)
        }
        val retMap = HashMap<Pair<Double, Double>, List<AccountBook>>()
        nowMap.forEach {
            retMap[it.key] = it.value
        }
        return retMap
    }

    fun hashMapToMarkerMap(dataMap: HashMap<Pair<Double, Double>, List<AccountBook>>): HashMap<Pair<Double, Double>, Pair<String, String>> {
        val markerMap = HashMap<Pair<Double, Double>, Pair<String, String>>()
        dataMap.forEach {
            markerMap[it.key] = Pair(
                it.value[0].address,
                "${it.value.sumOf { accountBook -> accountBook.money }}원"
            )
        }
        return markerMap
    }

    //viewModel 공유하면 다시 map화면 돌아왔을때 init
    fun setInitData() {
        byteMoneyType.value = EXPENSE
        intStartMoney.value = InitMoneyFilter.Start.money
        intEndMoney.value = InitMoneyFilter.End.money
        startYear = NOW_YEAR
        startMonth = NOW_MONTH
        startDay = 1
        endYear = NOW_YEAR
        endMonth = NOW_MONTH
        endDay = END_DAY
        // 화면에 보이는 위도/경도로 설정 해야함
        startLatitude = MIN_LAT.toFloat()
        startLongitude = MIN_LNG.toFloat()
        endLatitude = MAX_LAT.toFloat()
        endLongitude = MAX_LNG.toFloat()
        initLoadCategory()

        isMoneyFilterChange.value = false
        isPeriodFilterChange.value = false
        isCategoryBackgroundChange.value = false
    }

    private fun initLoadCategory() {
        viewModelScope.launch {
            val deferredExpenseCategory = async { repository.loadCategoryList(EXPENSE) }
            val deferredIncomeCategory = async { repository.loadCategoryList(INCOME) }

            val expenseCategory = deferredExpenseCategory.await()
            val incomeCategory = deferredIncomeCategory.await()

            categoryExpenseList.value = expenseCategory
            categoryIncomeList.value = incomeCategory
            categoryIDList.value = expenseCategory.map { it.id }.toMutableList()
            loadFilterData()

            incomeCategoryID = incomeCategory.map { it.id }
            expenseCategoryID = expenseCategory.map { it.id }
        }
    }

    fun setCategoryIDList(moneyType: Byte) {
        when (moneyType) {
            INCOME -> categoryIDList.value =
                categoryIncomeList.value?.map { it.id }?.toMutableList()
            EXPENSE -> categoryIDList.value =
                categoryExpenseList.value?.map { it.id }?.toMutableList()
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

        isPeriodFilterChange.value = !(startYear == NOW_YEAR &&
                startMonth == NOW_MONTH &&
                startDay == 1 &&
                endYear == NOW_YEAR &&
                endMonth == NOW_MONTH &&
                endDay == END_DAY)

        periodFilterBtnText =
            "${intToStringDate(startYear, startMonth, startDay)} ~ ${
                intToStringDate(endYear, endMonth, endDay)
            }"
    }

    fun changeMoneyFilterBtn() {
        val start = intStartMoney.value ?: InitMoneyFilter.Start.money
        val end = intEndMoney.value ?: InitMoneyFilter.End.money

        isMoneyFilterChange.value =
            !(start == InitMoneyFilter.Start.money && end == InitMoneyFilter.End.money)

        moneyFilterBtnText = "${formatter.format(start)} ~ ${
            if (end == Int.MAX_VALUE) "1,000,000 이상" else formatter.format(end)
        }"
    }

    fun changeCategoryBackground() {
        when (byteMoneyType.value) {
            INCOME -> {
                isCategoryBackgroundChange.value = incomeCategoryID != categoryIDList.value
            }
            EXPENSE -> {
                isCategoryBackgroundChange.value = expenseCategoryID != categoryIDList.value
            }
            else -> Timber.e("changeCategoryBackground byteMoneyType is null")
        }
    }
}
