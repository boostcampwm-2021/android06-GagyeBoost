package com.example.gagyeboost.ui.home

import android.location.Address
import android.location.Geocoder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.model.data.nothingEmoji
import kotlinx.coroutines.launch

class AddViewModel(private val repository: Repository) : ViewModel() {
    private val _selectedCategoryIcon = MutableLiveData("🍚")
    val selectedCategoryIcon: LiveData<String> = _selectedCategoryIcon

    val categoryName = MutableLiveData("")

    private var selectedCategoryId = -1

    val money = MutableLiveData("0")

    private val _categoryList = MutableLiveData<List<Category>>()
    val categoryList: LiveData<List<Category>> = _categoryList

    val content = MutableLiveData("")

    private var _categoryType = EXPENSE
    val categoryType get() = _categoryType

    var dateString = ""

    val searchAddress = MutableLiveData<String>()

    val selectedAddress = MutableLiveData<PlaceDetail>()

    lateinit var userLocation: Address

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

    //TODO 데이터 추가 : MoneyType, latitude, longitude, address, content
    fun addAccountBookData() {
        if (dateString.isEmpty()) return
        viewModelScope.launch {
            val splitedStr = dateString.split('/')
            repository.addAccountBookData(
                AccountBook(
                    moneyType = _categoryType,
                    money = if (money.value != null) money.value!!.toInt() else 0,
                    category = selectedCategoryId,
                    address = "${selectedAddress.value?.formattedAddress} ${selectedAddress.value?.name}",
                    latitude = selectedAddress.value?.geometry?.location?.lat?.toFloat()
                        ?: userLocation.latitude.toFloat(),
                    longitude = selectedAddress.value?.geometry?.location?.lng?.toFloat()
                        ?: userLocation.longitude.toFloat(),
                    content = content.value ?: "",
                    year = splitedStr[0].toInt(),
                    month = splitedStr[1].toInt(),
                    day = splitedStr[2].toInt()
                )
            )
            //TODO 달력 데이터 갱신
        }
    }

    fun loadCategoryList() {
        viewModelScope.launch {
            _categoryList.value = repository.loadCategoryList(categoryType)
        }
    }

    fun afterMoneyTextChanged() {
        if (money.value.isNullOrEmpty()) money.value = "0"

        money.value = money.value?.replaceFirst("^0+(?!$)".toRegex(), "")
    }

    fun getFormattedMoneyText(): String {
        return formatter.format(money.value?.toIntOrNull() ?: 0) + "원"
    }

    fun getFormattedMoneyText(money: Int) = formatter.format(money) + "원"

    fun getAddress(geocoder: Geocoder): List<Address> =
        geocoder.getFromLocationName(searchAddress.value, 20)

    fun getPlaceListData(input: String): LiveData<Result<List<PlaceDetail>>> {
        val data = MutableLiveData<Result<List<PlaceDetail>>>()

        viewModelScope.launch {
            val response = repository.getPlaceListFromKeyword(input)
            if (response.isSuccessful) {
                val body = response.body()

                body?.let {
                    if (it.status == "OK") {
                        data.postValue(Result.success(body.results))
                    } else {
                        data.postValue(Result.failure(Throwable()))
                    }
                }
            } else {
                data.postValue(Result.failure(Throwable()))
            }
        }

        return data
    }
}
