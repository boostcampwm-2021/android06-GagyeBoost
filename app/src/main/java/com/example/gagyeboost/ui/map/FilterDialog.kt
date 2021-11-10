package com.example.gagyeboost.ui.map

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.DialogFilterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*


class FilterDialog(context: Context, val viewModel: MapViewModel) :
    BottomSheetDialog(context), DatePickerDialog.OnDateSetListener {

    lateinit var binding: DialogFilterBinding

    var startYear: Int = 2021

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_filter,
            null,
            false
        )
        setContentView(binding.root)
        onClick()
    }

    private fun onClick() {
        binding.tvPeriodStartBody.setOnClickListener {
            showDatePickerDialog()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
        val datePickerDialog = DatePickerDialog(context, this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val date = "${year}/${month}/${dayOfMonth}"
        viewModel.startYear = year
    }
}
