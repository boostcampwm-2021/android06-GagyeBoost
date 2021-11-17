package com.example.gagyeboost.ui.address

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.gagyeboost.model.Repository
import com.google.android.gms.maps.model.LatLng

class AddressResultViewModel(private val repository: Repository) : ViewModel() {

    val searchKeyword = MutableLiveData<String>()

    fun fetchPlaceListData(input: String, latLng: LatLng, callback: (Boolean) -> Unit) =
        repository.fetchPlaceListFromKeyword(input, latLng, callback).cachedIn(viewModelScope)
}
