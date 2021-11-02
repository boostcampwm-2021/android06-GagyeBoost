package com.example.gagyeboost.ui.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gagyeboost.model.Repository

class HomeViewModel(val repository: Repository): ViewModel() {

    private val _month = MutableLiveData<Int>()
}