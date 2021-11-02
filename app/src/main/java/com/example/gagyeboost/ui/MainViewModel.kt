package com.example.gagyeboost.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(private val repository: Repository) : ViewModel() {

    private val _income = MutableLiveData<String>()
    val income get() = _income

    private val _expense = MutableLiveData<String>()
    val expense get() = _expense

    private val _result = MutableLiveData<String>()
    val result get() = _result

    private val formatter = DecimalFormat("###,###")

    private val dateFormatter = SimpleDateFormat("yyyy MM dd", Locale.getDefault())
    private val date =
        dateFormatter.format(Date(System.currentTimeMillis())).split(" ").map { it.toInt() }

    fun getMonthIncome() {
        viewModelScope.launch {
            repository.getMonthIncome(date[0], date[1])?.let {
                _income.postValue(formatter.format(it) + "원")
            } ?: _income.postValue("0")
        }
    }

    fun getMonthExpense() {
        viewModelScope.launch {
            repository.getMonthExpense(date[0], date[1])?.let {
                _expense.postValue(formatter.format(it) + "원")
            } ?: _expense.postValue("0원")
        }
    }

    fun setTotalMoney() {
        viewModelScope.launch {
            val income = repository.getMonthIncome(date[0], date[1])
            val expense = repository.getMonthExpense(date[0], date[1])

            val result = expense?.let {
                formatter.format(income?.minus(it) ?: 0) + "원"
            } ?: formatter.format(income ?: 0) + "원"

            _result.postValue(result)
        }
    }
}
