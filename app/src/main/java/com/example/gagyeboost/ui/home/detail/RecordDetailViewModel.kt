package com.example.gagyeboost.ui.home.detail

import androidx.lifecycle.*
import com.example.gagyeboost.common.*
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.*
import com.google.android.gms.maps.model.Marker
import kotlinx.coroutines.launch

class RecordDetailViewModel(private val repository: Repository, private val accountBookId: Int) :
    ViewModel() {

    private val _accountBookData = MutableLiveData<RecordDetailData>()
    val accountBookData: LiveData<RecordDetailData> = _accountBookData

    private val _date = MutableLiveData<String>()
    val date: LiveData<String> = _date

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    private val _category = MutableLiveData<Category>()
    val category: LiveData<Category> = _category

    private val _selectedLocation = MutableLiveData(MyItem(MAX_LAT, MAX_LNG, "", ""))
    val selectedLocation: LiveData<MyItem> = _selectedLocation

    private val _searchPlaceList = MutableLiveData<List<PlaceDetail>>()
    val searchPlaceList: LiveData<List<PlaceDetail>> = _searchPlaceList

    val money = MutableLiveData<Int>()
    val content = MutableLiveData<String>()
    val placeDetail = Transformations.map(_accountBookData) {
        val place =
            PlaceDetail("", "", "", it.address, it.latitude.toString(), it.longitude.toString())
        setPlaceList(listOf(place))
        place
    }

    init {
        viewModelScope.launch {
            val data = repository.loadRecordDetailData(accountBookId)
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

    fun deleteAccountBookData(id: Int) {
        viewModelScope.launch {
            repository.deleteAccountBookData(id)
        }
    }

    fun updateAccountBookData() {
        viewModelScope.launch {
            val strDate = (_date.value ?: "2021.07.19").split(".").map { it.toInt() }
            val updatedAccountBookData = AccountBook(
                accountBookId,
                _accountBookData.value?.moneyType ?: EXPENSE,
                money.value ?: 0,
                _category.value?.id ?: 0,
                _selectedLocation.value?.position?.latitude ?: DEFAULT_LAT,
                _selectedLocation.value?.position?.longitude ?: DEFAULT_LNG,
                _selectedLocation.value?.title ?: "",
                content.value ?: "",
                strDate[0],
                strDate[1],
                strDate[2]
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
        _searchPlaceList.value = placeList
    }

    fun setSelectedPlace(marker: Marker?) {
        val location = if (marker != null) {
            MyItem(
                marker.position.latitude,
                marker.position.longitude,
                marker.title ?: "",
                marker.snippet ?: ""
            )
        } else {
            MyItem(MAX_LAT, MAX_LNG, "", "")
        }
        _selectedLocation.value = location
    }
}
