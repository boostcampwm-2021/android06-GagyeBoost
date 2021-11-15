package com.example.gagyeboost.ui.statstics

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.FragmentStatisticsBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.NumberPickerDialog
import com.github.mikephil.charting.charts.LineChart
import org.koin.androidx.viewmodel.ext.android.viewModel


class StatisticsFragment :
    BaseFragment<FragmentStatisticsBinding>(com.example.gagyeboost.R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModel()
    private lateinit var dialog: NumberPickerDialog
    private lateinit var chartDaily: LineChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setListeners()
        setObservers()
    }

    private fun initView() {
        dialog = NumberPickerDialog(binding.root.context)

        binding.viewModel = viewModel
        binding.tvYearAndMonth.setOnClickListener {
            setDialog()
        }

        chartDaily = binding.chartDailyStat
        setDailyChart()
    }

    private fun setObservers() {
        viewModel.selectedMoneyType.observe(viewLifecycleOwner) {
            viewModel.setDailyChartData()
        }

        viewModel.yearMonthPair.observe(viewLifecycleOwner) {
            viewModel.setDailyChartData()
        }
    }
    
    private fun setListeners() {
        binding.toggleGroupMoneyType.addOnButtonCheckedListener { _, checkedId, _ ->
            when (checkedId) {
                R.id.btn_expense -> viewModel.setSelectedMoneyType(EXPENSE)
                R.id.btn_income -> viewModel.setSelectedMoneyType(INCOME)
            }
        }
    }

    private fun setDialog() {
        dialog.window?.setGravity(Gravity.TOP)
        dialog.show()

        with(dialog.binding) {
            dialog.setOnCancelListener {
                viewModel.setYearAndMonth(
                    npYear.value,
                    npMonth.value
                )
            }
            tvAgree.setOnClickListener {
                viewModel.setYearAndMonth(
                    npYear.value,
                    npMonth.value
                )
                dialog.dismiss()
            }
            tvCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun setDailyChart() {

    }
}
