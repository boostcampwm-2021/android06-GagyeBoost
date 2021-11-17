package com.example.gagyeboost.ui.home.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.DEFAULT_LAT
import com.example.gagyeboost.common.DEFAULT_LNG
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.DateDetailItem
import kotlinx.coroutines.launch
import java.text.DecimalFormat

class RecordDetailViewModel(private val repository: Repository, private val accountBookId: Int) :
    ViewModel() {

    private val _accountBookData = MutableLiveData<AccountBook>()
    val accountBookData: LiveData<AccountBook> = _accountBookData

    val dateDetailItem = MutableLiveData<DateDetailItem>()

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    private val formatter = DecimalFormat("###,###")

    fun setAccountBookData(callback: () -> Unit) {
        viewModelScope.launch {
            val accountBookData = repository.loadAccountBookData(accountBookId)
            val categoryId = accountBookData.category
            val category = repository.loadCategoryData(categoryId)
            _category.value = category
            _accountBookData.value = accountBookData

            dateDetailItem.value = DateDetailItem(
                accountBookId,
                category.emoji,
                category.categoryName,
                accountBookData.content,
                formatter.format(accountBookData.money),
                accountBookData.moneyType == INCOME,
            )

            setDate(accountBookData.year, accountBookData.month, accountBookData.day)

            callback()
        }
    }

    fun deleteAccountBookData(id: Int) {
        viewModelScope.launch {
            repository.deleteAccountBookData(id)
        }
    }

    fun updateAccountBookData() {
        viewModelScope.launch {
            with(dateDetailItem.value) {
                if (this == null) return@launch

                val strDate = _date.value ?: "2021.07.19"
                val updatedAccountBookData = AccountBook(
                    accountBookId,
                    if (moneyType) INCOME else EXPENSE,
                    money.replace(",", "").toIntOrNull() ?: 0,
                    _category.value?.id ?: 0,
                    DEFAULT_LAT.toFloat(),
                    DEFAULT_LNG.toFloat(),
                    "부스트캠프",
                    dateDetailItem.value?.content ?: "",
                    strDate.split(".")[0].toInt(),
                    strDate.split(".")[1].toInt(),
                    strDate.split(".")[2].toInt()
                )

                repository.updateAccountBookData(updatedAccountBookData)
            }
        }
    }

    fun setDate(year: Int, month: Int, day: Int) {
        _date.value = "$year.$month.$day"
    }

    fun loadCategoryList() {
        val categoryType = if (dateDetailItem.value?.moneyType == true) INCOME else EXPENSE

        viewModelScope.launch {
            _categoryList.value = repository.loadCategoryList(categoryType)
        }
    }

    fun setCategory(category: Category) {
        _category.value = category
    }
}
