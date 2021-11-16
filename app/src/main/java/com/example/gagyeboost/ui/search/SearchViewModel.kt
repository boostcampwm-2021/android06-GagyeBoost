package com.example.gagyeboost.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.common.DEFAULT_FILTER
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.Filter
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: Repository) : ViewModel() {
    private val _keyword = MutableLiveData("")
    val keyword: LiveData<String> = _keyword

    private val _filter = MutableLiveData(DEFAULT_FILTER)
    val filter: LiveData<Filter> = _filter

    fun loadFilterData(){
        viewModelScope.launch{
            val deferredDataList=async{
                repository.loadFilterDataWithKeyword(filter.value?:DEFAULT_FILTER,keyword.value?:"")
            }
            val dataList=deferredDataList.await()

        }
    }

}