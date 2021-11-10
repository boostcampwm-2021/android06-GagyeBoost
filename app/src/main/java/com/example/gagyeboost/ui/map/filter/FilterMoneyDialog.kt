package com.example.gagyeboost.ui.map.filter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.DialogFilterPeriodBinding
import com.example.gagyeboost.ui.map.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class FilterMoneyDialog(context: Context, val viewModel: MapViewModel) :
    BottomSheetDialog(context) {

    lateinit var binding: DialogFilterPeriodBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_filter_period,
            null,
            false
        )
        setContentView(binding.root)
        binding.viewModel = viewModel

        var left = binding.rsMoney.valueFrom.toInt()
        var right = binding.rsMoney.valueTo.toInt()

        with(binding.rsMoney) {
            val start = viewModel.intStartMoney.value?.toFloat() ?: 0f
            val end = if (viewModel.intEndMoney.value == Int.MAX_VALUE) {
                1000000f
            } else {
                viewModel.intEndMoney.value?.toFloat() ?: 300000f
            }

            values = listOf(start, end)
            addOnChangeListener { _, value, _ ->
                when (focusedThumbIndex) {
                    0 -> left = value.toInt()
                    1 -> right = value.toInt()
                }
                if (left == valueFrom.toInt() && right == valueTo.toInt()) {
                    viewModel.setMoney(left, Int.MAX_VALUE)
                } else {
                    viewModel.setMoney(left, right)
                }
            }
        }

        binding.btnFilterApply.setOnClickListener {
            viewModel.loadFilterData()
            dismiss()
        }
    }
}
