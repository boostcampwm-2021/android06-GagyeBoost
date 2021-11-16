package com.example.gagyeboost.ui.map.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.DialogFilterMoneyBinding
import com.example.gagyeboost.ui.map.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterMoneyDialog : BottomSheetDialogFragment() {

    private var _binding: DialogFilterMoneyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_filter_money,
            null,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
