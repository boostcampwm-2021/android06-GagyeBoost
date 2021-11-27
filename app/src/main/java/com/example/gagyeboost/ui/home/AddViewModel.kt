package com.example.gagyeboost.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.MAX_LAT
import com.example.gagyeboost.common.MAX_LNG
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.*
import kotlinx.coroutines.launch

class AddViewModel(private val repository: Repository) : ViewModel() {

    private val _selectedCategoryIcon = MutableLiveData("🍚")
    val selectedCategoryIcon: LiveData<String> = _selectedCategoryIcon

    val categoryName = MutableLiveData("")
    private var selectedCategoryId = -1

    val money = MutableLiveData(0)

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    val content = MutableLiveData("")

    private var _categoryType = EXPENSE
    val categoryType get() = _categoryType

    var dateString = ""

    val searchAddress = MutableLiveData<String>()

    //var selectedLocation: PlaceDetail? = null
    private val _selectedLocation = MutableLiveData(MyItem(MAX_LAT, MAX_LNG, "", ""))
    val selectedLocation: LiveData<MyItem> = _selectedLocation

    private val _selectedLocationList = MutableLiveData<List<PlaceDetail>>()
    val selectedLocationList: LiveData<List<PlaceDetail>> = _selectedLocationList

    private val _isEdit = MutableLiveData(false)
    val isEdit: LiveData<Boolean> get() = _isEdit

    fun setSelectedIcon(icon: String) {
        _selectedCategoryIcon.value = icon
    }

    fun addCategory() {
        viewModelScope.launch {
            repository.addCategoryData(
                Category(
                    categoryName = categoryName.value ?: "",
                    emoji = _selectedCategoryIcon.value ?: nothingEmoji,
                    moneyType = _categoryType
                )
            )
            loadCategoryList()
            resetSelectedCategory()
        }
    }

    fun setCategoryType(type: Byte) {
        _categoryType = type
    }

    fun resetSelectedCategory() {
        categoryName.value = ""
        _selectedCategoryIcon.value = "\uD83C\uDF5A"
        selectedCategoryId = -1
    }

    // 선택한 카테고리를 인자로 UpdateCategory에 표시(카테고리 long click 시 호출)
    fun setCategoryData(category: Category) {
        selectedCategoryId = category.id
        categoryName.value = category.categoryName
        _selectedCategoryIcon.value = category.emoji
    }

    fun updateCategory() {
        viewModelScope.launch {
            repository.updateCategoryData(
                Category(
                    selectedCategoryId,
                    categoryName.value ?: "",
                    selectedCategoryIcon.value ?: nothingEmoji,
                    _categoryType
                )
            )
            loadCategoryList()
            resetSelectedCategory()
        }
    }

    fun addAccountBookData() {
        if (dateString.isEmpty()) return
        viewModelScope.launch {
            val splitStr = dateString.split('/')
            val data = AccountBook(
                moneyType = _categoryType,
                money = money.value ?: 0,
                category = selectedCategoryId,
                address = selectedLocation.value?.title ?: "",
                latitude = selectedLocation.value?.position?.latitude ?: MAX_LAT,
                longitude = selectedLocation.value?.position?.longitude ?: MAX_LNG,
                content = content.value ?: "",
                year = splitStr[0].toInt(),
                month = splitStr[1].toInt(),
                day = splitStr[2].toInt()
            )
            repository.addAccountBookData(data)
        }
        money.value = 0
    }

    fun loadCategoryList() {
        viewModelScope.launch {
            _categoryList.value = repository.loadCategoryList(categoryType)
        }
    }

    fun setPlaceList(placeList: List<PlaceDetail>) {
        _selectedLocationList.value = placeList
    }

    fun setSelectedPlace(location: MyItem) {
        _selectedLocation.value = location
    }

    fun resetLocation() {
        _selectedLocationList.value = listOf()
        _selectedLocation.value = MyItem(MAX_LAT, MAX_LNG, "", "")
        searchAddress.value = ""
    }

    fun resetCategoryFragmentData() {
        content.value = ""
        _categoryList.value = listOf()
        _categoryType = EXPENSE
    }

    fun resetAllData() {
        resetSelectedCategory()
        money.value = 0
        resetCategoryFragmentData()
        dateString = ""
        resetLocation()
    }

    fun deleteCategory(callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            if (!repository.isExistAccountBookDataByCategory(selectedCategoryId)) {
                repository.deleteCategory(selectedCategoryId)
                callback(true)
            } else {
                callback(false)
            }
        }
    }

    fun doEdit(isEdit: Boolean) {
        _isEdit.value = isEdit
    }
}
