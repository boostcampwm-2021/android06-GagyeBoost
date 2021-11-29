package com.example.gagyeboost.ui.home.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.*
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.*
import kotlinx.coroutines.launch
import timber.log.Timber

class RecordDetailViewModel(private val repository: Repository, private val accountBookId: Int) :
    ViewModel() {

    private val _accountBookData = MutableLiveData<RecordDetailData>()
    val accountBookData: LiveData<RecordDetailData> = _accountBookData

    val dateDetailData = MutableLiveData<DateDetailData>()

    val dateDetailItemMoney = MutableLiveData<Int>()

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    private val _selectedLocation = MutableLiveData(MyItem(MAX_LAT, MAX_LNG, "", ""))
    val selectedLocation: LiveData<MyItem> = _selectedLocation

    private val _selectedLocationList = MutableLiveData<List<PlaceDetail>>()
    val selectedLocationList: LiveData<List<PlaceDetail>> = _selectedLocationList

    val searchAddress = MutableLiveData<String>()
    val money = MutableLiveData<Int>()
    val content = MutableLiveData<String>()

    init {
        viewModelScope.launch {
            val data = repository.exLoadAccountBookData(accountBookId)
            Timber.e(accountBookData.value.toString())
            _accountBookData.value = data
            _category.value =
                Category(data.categoryID, data.categoryName, data.emoji, data.moneyType)
            setDate(data.year, data.month, data.day)
            content.value = data.content
            money.value = data.money

            val categoryList = repository.loadCategoryList(data.moneyType)
            _categoryList.value = categoryList
        }
    }

    fun setAccountBookData(callback: () -> Unit) {
        viewModelScope.launch {
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
            val strDate = _date.value ?: "2021.07.19"
            val updatedAccountBookData = AccountBook(
                accountBookId,
                _accountBookData.value?.moneyType ?: EXPENSE,
                money.value ?: 0,
                _category.value?.id ?: 0,
                selectedLocation.value?.position?.latitude ?: DEFAULT_LAT,
                selectedLocation.value?.position?.longitude ?: DEFAULT_LNG,
                selectedLocation.value?.title ?: "",
                content.value ?: "",
                strDate.split(".")[0].toInt(),
                strDate.split(".")[1].toInt(),
                strDate.split(".")[2].toInt()
            )

            repository.updateAccountBookData(updatedAccountBookData)
        }

    }

    fun setDate(year: Int, month: Int, day: Int) {
        _date.value = "$year.$month.$day"
    }

    fun setCategory(category: Category) {
        _category.value = category
    }

    fun setPlaceList(placeList: List<PlaceDetail>) {
        _selectedLocationList.value = placeList
    }

    fun setSelectedPlace(location: MyItem) {
        _selectedLocation.value = location
    }

    fun resetLocation() {
        _selectedLocationList.value = listOf()
        _selectedLocation.value = MyItem(MAX_LAT, MAX_LAT, "", "")
        searchAddress.value = ""
    }
}
