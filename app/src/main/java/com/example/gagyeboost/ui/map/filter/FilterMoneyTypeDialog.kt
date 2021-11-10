package com.example.gagyeboost.ui.map.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.DialogFilterMoneyTypeBinding
import com.example.gagyeboost.ui.map.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class FilterMoneyTypeDialog(context: Context, val viewModel: MapViewModel) :
    BottomSheetDialog(context) {

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
}
