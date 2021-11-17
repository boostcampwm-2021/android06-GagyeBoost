package com.example.gagyeboost.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.DEFAULT_FILTER
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.Filter
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.util.*

class SearchViewModel(private val repository: Repository) : ViewModel() {

    val keyword = MutableLiveData("")

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

    fun loadFilterData() {
        viewModelScope.launch {
            /*
            val deferredDataList=async{
                repository.loadFilterDataWithKeyword(filter.value?:DEFAULT_FILTER,keyword.value?:"")
            }
            val dataList=deferredDataList.await()

             */

        }
    }

}