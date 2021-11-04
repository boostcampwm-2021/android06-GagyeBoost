package com.example.gagyeboost.ui.home

import android.view.Gravity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import kotlinx.coroutines.launch
import java.util.*

class HomeViewModel(val repository: Repository) : ViewModel() {

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    private val _month = MutableLiveData<Int>()
    private val _year = MutableLiveData<Int>()

    private val calendar = CustomCalendar()

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH))
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

        _yearAndMonth.value = stringDate
        _year.value = year
        _month.value = month
    }

    fun startDialog(dialog: NumberPickerDialog) {
        dialog.window?.setGravity(Gravity.TOP)
        dialog.show()
    }

    fun loadAllDayDataInMonth() {
        viewModelScope.launch {
            val dateItemList = calendar.datesInMonth.map { date ->
                val accountDataList =
                    repository.loadDayData(_year.value ?: 0, _month.value ?: 0, date)

                var totalExpense = 0
                var totalIncome = 0

                accountDataList.forEach { record ->
                    when (record.moneyType) {
                        0.toByte() -> totalExpense += record.money
                        1.toByte() -> totalIncome += record.money
                    }
                }
                DateItem(totalExpense, totalIncome, date, _year.value ?: 0, _month.value ?: 0)
            }
        }
    }
}
