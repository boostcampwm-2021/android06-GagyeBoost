package com.example.gagyeboost.ui.map

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Filter
import kotlinx.coroutines.launch
import java.util.*

class MapViewModel(private val repository: Repository) : ViewModel() {

    // 양방향 데이터 바인딩
    private var filterMoneyType: Byte = EXPENSE
    var startMoney: Int = 0
    var endMoney: Int = Int.MAX_VALUE
    val categoryList = MutableLiveData<List<Int>>()
    var startYear: Int = 1900
    var startMonth: Int = 1
    var startDay: Int = 1
    var endYear: Int = 2500
    var endMonth: Int = 12
    var endDay: Int = 31
    var startLatitude: Float = 37.566029F
    var startLongitude: Float = 126.897156F
    var endLatitude: Float = 37.481645F
    var endLongitude: Float = 127.213158F

    val filterData = MutableLiveData<List<AccountBook>>()

    val test = MutableLiveData<String>()

    // 데이터 바인딩
    fun expenseBtnOnClick() {
        filterMoneyType = EXPENSE
        loadFilterData()
    }

    fun incomeBtnOnClick() {
        filterMoneyType = INCOME
        loadFilterData()
    }

    //viewModel 공유하면 다시 map화면 돌아왔을때 init
    fun setInitData() {
        loadAllCategoryID()
        filterMoneyType = EXPENSE
        startMoney = 0
        endMoney = Int.MAX_VALUE
        startYear = Calendar.getInstance().get(Calendar.YEAR)
        startMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        startDay = 1
        endYear = startYear
        endMonth = startMonth
        endDay = Calendar.getInstance().getActualMaximum(Calendar.DATE)
        // 화면에 보이는 위도/경도로 설정 해야함
        startLatitude = 0.0f
        startLongitude = 0.0f
        endLatitude = 0.0f
        endLongitude = 0.0f
    }

    fun loadAllCategoryID() {
        viewModelScope.launch {
            categoryList.postValue(repository.loadAllCategoryID())
        }
    }


    fun loadFilterData() {
        viewModelScope.launch {
            val data = setFilter()
            Log.e("data", data.toString())
            val filter = repository.loadFilterData(data)
            Log.e("filterData-viewModel", filter.toString())
            filterData.postValue(filter)
        }
    }

    private fun setFilter() = Filter(
        INCOME,
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
        startMoney,
        endMoney,
        categoryList.value ?: listOf()
    )


}
