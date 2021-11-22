package com.example.gagyeboost.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.Filter
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(private val repository: Repository) : ViewModel() {

    val keyword = MutableLiveData("")

    /* private val _selectedType = MutableLiveData(EXPENSE)
    val selectedType: LiveData<Byte> = _selectedType*/

    private val _startYear = MutableLiveData(Calendar.getInstance().get(Calendar.YEAR))
    val startYear: LiveData<Int> = _startYear

    private val _startMonth = MutableLiveData(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val startMonth: LiveData<Int> = _startMonth

    private val _startDay = MutableLiveData(1)
    val startDay: LiveData<Int> = _startDay

    private val _endYear = MutableLiveData(Calendar.getInstance().get(Calendar.YEAR))
    val endYear: LiveData<Int> = _endYear

    private val _endMonth = MutableLiveData(Calendar.getInstance().get(Calendar.MONTH) + 1)
    val endMonth: LiveData<Int> = _endMonth

    private val _endDay =
        MutableLiveData(Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH))
    val endDay: LiveData<Int> = _endDay

    private val _startMoney = MutableLiveData(0)
    val startMoney: LiveData<Int> = _startMoney

    private val _endMoney = MutableLiveData(1000000)
    val endMoney: LiveData<Int> = _endMoney

    private val _selectedCategory = MutableLiveData<List<Category>>(listOf())
    val selectedCategory: LiveData<List<Category>> = _selectedCategory

    fun loadFilterData() {
        viewModelScope.launch {
            val filter = Filter(
                null,
                startYear.value ?: 1970,
                startMonth.value ?: 1,
                startDay.value ?: 1,
                endYear.value ?: 2500,
                endMonth.value ?: 12,
                endDay.value ?: 31,
                0f,
                0f,
                200f,
                200f,
                startMoney.value ?: 0,
                endMoney.value ?: 1000000,
                (selectedCategory.value ?: listOf()).map { it.id }
            )
            val deferredDataList = async {
                repository.loadFilterDataWithKeyword(filter, keyword.value ?: "")
            }
            val dataList = deferredDataList.await()
        }
    }

    /* fun setSelectedType(type: Byte) {
        _selectedType.value = type
    } */

    fun resetData() {
        keyword.value = ""
        _startYear.value = Calendar.getInstance().get(Calendar.YEAR)
        _startMoney.value = Calendar.getInstance().get(Calendar.MONTH) + 1
        _startDay.value = 1
        _endYear.value = Calendar.getInstance().get(Calendar.YEAR)
        _endMonth.value = Calendar.getInstance().get(Calendar.MONTH) + 1
        _endDay.value = Calendar.getInstance().getActualMaximum(Calendar.DAY_OF_MONTH)
        _startMoney.value = 0
        _endMoney.value = 1000000
        _selectedCategory.value = listOf()
    }

    fun setStartDate(year: Int, month: Int, day: Int) {
        val startCode = dateToDateCode(year, month, day)
        val endCode = dateToDateCode(endYear.value ?: 2500, endMonth.value ?: 12, endDay.value ?: 1)
        _startYear.value = year
        _startMonth.value = month
        _startDay.value = day
        if (startCode > endCode) {
            _endYear.value = year
            _endMonth.value = month
            _endDay.value = day
        }
    }

    fun setEndDate(year: Int, month: Int, day: Int) {
        val startCode =
            dateToDateCode(startYear.value ?: 1970, startMonth.value ?: 1, startDay.value ?: 1)
        val endCode = dateToDateCode(year, month, day)
        _endYear.value = year
        _endMonth.value = month
        _endDay.value = day
        if (startCode > endCode) {
            _startYear.value = year
            _startMonth.value = month
            _startDay.value = day
        }
    }

    private fun dateToDateCode(year: Int, month: Int, day: Int) = year * 10000 + month * 100 + day
}
