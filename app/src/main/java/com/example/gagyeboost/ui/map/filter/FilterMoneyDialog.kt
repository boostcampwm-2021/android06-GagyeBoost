package com.example.gagyeboost.ui.map.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.DialogFilterMoneyBinding
import com.example.gagyeboost.model.data.InitMoneyFilter
import com.example.gagyeboost.ui.map.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

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

        val initStart = viewModel.intStartMoney.value ?: InitMoneyFilter.Start.money
        val initEnd = viewModel.intEndMoney.value ?: InitMoneyFilter.End.money

        with(binding.rsMoney) {
            var start = initStart.toFloat()
            var end = if (initEnd == Int.MAX_VALUE) 1000000f else initEnd.toFloat()

            values = listOf(start, end)

            addOnChangeListener { _, value, _ ->
                when (focusedThumbIndex) {
                    0 -> start = value
                    1 -> end = value
                }
                if (right == InitMoneyFilter.End.money) {
                    viewModel.setMoney(start.toInt(), Int.MAX_VALUE)
                } else {
                    viewModel.setMoney(start.toInt(), end.toInt())
                }
            }
        }

        binding.btnFilterApply.setOnClickListener {
            viewModel.loadFilterData()
            viewModel.changeMoneyBackground()
            dismiss()
        }

        this.dialog?.setOnCancelListener {
            viewModel.setMoney(initStart, initEnd)
        } ?: Timber.e("setOnCancelListener dialog is null")

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
