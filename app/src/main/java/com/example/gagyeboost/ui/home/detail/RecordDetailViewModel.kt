package com.example.gagyeboost.ui.home.detail

import androidx.lifecycle.*
import com.example.gagyeboost.common.DEFAULT_LAT
import com.example.gagyeboost.common.DEFAULT_LNG
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.DateDetailItem
import com.example.gagyeboost.model.data.PlaceDetail
import kotlinx.coroutines.launch

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

    var placeDetail: PlaceDetail? = null

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
                accountBookData.money,
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

    fun updateAccountBookData(placeDetail: PlaceDetail? = this.placeDetail) {
        viewModelScope.launch {
            with(dateDetailItem.value) {
                if (this == null) return@launch

                val strDate = _date.value ?: "2021.07.19"
                val updatedAccountBookData = AccountBook(
                    accountBookId,
                    if (moneyType) INCOME else EXPENSE,
                    money,
                    _category.value?.id ?: 0,
                    placeDetail?.lat?.toFloat() ?: accountBookData.value?.latitude
                    ?: DEFAULT_LAT.toFloat(),
                    placeDetail?.lng?.toFloat() ?: accountBookData.value?.longitude
                    ?: DEFAULT_LNG.toFloat(),
                    placeDetail?.addressName ?: "부스트캠프",
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
