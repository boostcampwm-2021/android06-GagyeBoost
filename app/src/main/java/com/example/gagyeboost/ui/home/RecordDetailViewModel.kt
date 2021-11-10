package com.example.gagyeboost.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.DateDetailItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class RecordDetailViewModel(private val repository: Repository, private val accountBookId: Int) :
    ViewModel() {

    private val _accountBookData = MutableLiveData<DateDetailItem>()
    val accountBookData: LiveData<DateDetailItem> = _accountBookData

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val formatter = DecimalFormat("###,###")

    init {
        setAccountBookData()
    }

    private fun setAccountBookData() {
        viewModelScope.launch {
            val accountBookData = repository.loadAccountBookData(accountBookId)
            val categoryId = accountBookData.category
            val category = repository.loadCategoryData(categoryId)

            _accountBookData.value = DateDetailItem(
                accountBookId,
                category.emoji,
                category.categoryName,
                accountBookData.content,
                formatter.format(accountBookData.money) + "원",
                accountBookData.moneyType == INCOME,
            )

            _date.value =
                "" + accountBookData.year + "." + accountBookData.month + "." + accountBookData.day
        }
    }

    fun deleteAccountBookData(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAccountBookData(id)
        }
    }

    fun updateAccountBookData() {
        viewModelScope.launch(Dispatchers.IO) {

        }
    }
}
