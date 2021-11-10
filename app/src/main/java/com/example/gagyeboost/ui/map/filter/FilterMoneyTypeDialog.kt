package com.example.gagyeboost.ui.map.filter

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.DatePicker
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.DialogFilterMoneyTypeBinding
import com.example.gagyeboost.ui.map.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.*

class FilterMoneyTypeDialog(context: Context, val viewModel: MapViewModel) :
    BottomSheetDialog(context), DatePickerDialog.OnDateSetListener {
    lateinit var binding: DialogFilterMoneyTypeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_filter_money_type,
            null,
            false
        )
        setContentView(binding.root)
        binding.viewModel = viewModel

        onClick()

    }

    private fun onClick() {
        binding.btnExpense.setOnClickListener {
            viewModel.byteMoneyType.value = EXPENSE
            viewModel.loadFilterData()
            dismiss()
        }
        binding.btnIncome.setOnClickListener {
            viewModel.byteMoneyType.value = INCOME
            viewModel.loadFilterData()
            dismiss()
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