package com.example.gagyeboost.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.gagyeboost.model.Repository
import java.util.*

class HomeViewModel(val repository: Repository): ViewModel() {

    private val _month = MutableLiveData<Int>()
    val month = Transformations.map(_month) { "${it}월" }

    init {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        setMonth(currentMonth)
    }

    fun setMonth(month: Int) {
        _month.value = month
    }

    fun setYear(year: Int) {}

}
