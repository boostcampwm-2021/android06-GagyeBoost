package com.example.gagyeboost.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.gagyeboost.model.Repository
import com.example.gagyeboost.model.data.Category

class MainViewModel(private val repository: Repository) : ViewModel() {
    private val _selectedCategory = MutableLiveData<String?>()
    val selectedCategory: LiveData<String?> = _selectedCategory

    fun setSelectedIcon(icon: String) {
        _selectedCategory.value = icon
    }
}
