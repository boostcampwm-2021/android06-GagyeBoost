package com.example.gagyeboost.ui.statstics

import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.common.ANIMATE_Y_TIME
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.FragmentStatisticsBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.NumberPickerDialog
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import org.koin.androidx.viewmodel.ext.android.viewModel


class StatisticsFragment :
    BaseFragment<FragmentStatisticsBinding>(com.example.gagyeboost.R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModel()
    private lateinit var dialog: NumberPickerDialog
    private lateinit var chartDaily: BarChart

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
    }

    private fun setObservers() {
        viewModel.selectedMoneyType.observe(viewLifecycleOwner) {
            viewModel.setDailyChartData()
        }

        viewModel.yearMonthPair.observe(viewLifecycleOwner) {
            viewModel.setDailyChartData()
        }

        viewModel.dailyChartData.observe(viewLifecycleOwner) {
            setDailyChart(it)
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

    private fun setDailyChart(chartData: List<Pair<Int, Int>>) {
        with(chartDaily) {
            // 그래프 뷰 설정
            description.isEnabled = false
            legend.isEnabled = false
            setTouchEnabled(false)
            setDrawGridBackground(false)
            setPinchZoom(false)

            // X축 설정
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawAxisLine(false)
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1F
            xAxis.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return "" + viewModel.yearMonthPair.value?.second + "." + value.toInt()
                }
            }

            // Y축 설정
            axisLeft.valueFormatter = object : ValueFormatter() {
                override fun getAxisLabel(value: Float, axis: AxisBase?): String {
                    return "" + value.toInt() + getString(R.string.chart_y_axis_unit)
                }
            }
            axisLeft.axisMinimum = 0F
            axisLeft.granularity = 1F
            axisRight.setDrawAxisLine(false)
            axisRight.isEnabled = false
            animateY(ANIMATE_Y_TIME)

            val chartDataSet = BarDataSet(
                chartData.map { BarEntry(it.first.toFloat(), it.second.toFloat()) },
                ""
            )
            chartDataSet.colors = ColorTemplate.VORDIPLOM_COLORS.toList()
            chartDataSet.setDrawValues(false)

            val resultData = BarData(chartDataSet)

            data = resultData
            notifyDataSetChanged()
        }
    }
}
