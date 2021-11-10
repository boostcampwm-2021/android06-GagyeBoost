package com.example.gagyeboost.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.AccountBook
import kotlinx.coroutines.launch

class RecordDetailViewModel(private val repository: Repository, private val accountBookId: Int) :
    ViewModel() {

    private val _accountBookInfo = MutableLiveData<AccountBook>()
    val accountBookInfo: LiveData<AccountBook> = _accountBookInfo

    fun setAccountBookInfo() {
        viewModelScope.launch {
            _accountBookInfo.value = repository.loadAccountBookData(accountBookId)
        }
    }

}