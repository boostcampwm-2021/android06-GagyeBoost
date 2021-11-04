package com.example.gagyeboost.ui.home

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.NumberPicker
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.DialogNumberPickerBinding
import java.util.*

class NumberPickerDialog(context: Context) : AlertDialog(context) {

    lateinit var binding: DialogNumberPickerBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_number_picker,
            null,
            false
        )
        setContentView(binding.root)
        setNumberPicker()
    }

    private fun setNumberPicker() {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        with(binding.npYear) {
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = currentYear - 10
            maxValue = currentYear + 1
            value = currentYear
        }

        with(binding.npMonth) {
            wrapSelectorWheel = false
            descendantFocusability = NumberPicker.FOCUS_BLOCK_DESCENDANTS
            minValue = 1
            maxValue = 12
            value = currentMonth
        }
    }
}
