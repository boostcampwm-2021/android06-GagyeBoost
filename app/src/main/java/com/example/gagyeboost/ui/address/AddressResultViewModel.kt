package com.example.gagyeboost.ui.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.PlaceDetail
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AddressResultViewModel(private val repository: Repository) : ViewModel() {

    val searchKeyword = MutableLiveData<String>()

    fun fetchPlaceListData(input: String, latLng: LatLng, callback: (Boolean) -> Unit) =
        repository.fetchPlaceListFromKeyword(input, latLng, callback).cachedIn(viewModelScope)

    fun loadPlaceListData(latLng: LatLng, callback: (placeList: List<PlaceDetail>) -> Unit) {
        viewModelScope.launch {
            //TODO 최대 요청 페이지 상수(마커 수) 정해야 함
            val REQUEST_PAGE = 3

            val placeMap = LinkedHashMap<String, PlaceDetail>()
            val defArr = Array(REQUEST_PAGE) {
                async {
                    repository.loadPlaceListFromKeyword(
                        searchKeyword.value ?: "", latLng, it + 1
                    )
                }
            }
            defArr.map { it ->
                (it.await()?.documents ?: listOf()).forEach {
                    placeMap[it.id] = it
                }
            }
            callback(placeMap.map { it.value })
        }
    }
}
