package com.example.gagyeboost.ui.home

import android.view.Gravity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gagyeboost.model.Repository
import java.util.*

class HomeViewModel(val repository: Repository): ViewModel() {

    private val currentYear = Calendar.getInstance().get(Calendar.YEAR)

    private val _yearAndMonth = MutableLiveData<String>()
    val yearAndMonth: LiveData<String> = _yearAndMonth

    init {
        setYearAndMonth(currentYear, Calendar.getInstance().get(Calendar.MONTH))
    }

    fun setYearAndMonth(year: Int, month: Int) {
        val stringDate = if (year == currentYear) "${month}월" else "${year}년 ${month}월"

        _yearAndMonth.value = stringDate
    }

    fun startDialog(dialog: NumberPickerDialog) {
        dialog.window?.setGravity(Gravity.TOP)
        dialog.show()
    }

}
