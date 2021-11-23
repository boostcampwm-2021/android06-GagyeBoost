package com.example.gagyeboost.ui.statstics

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.view.isGone
import com.example.gagyeboost.R
import com.example.gagyeboost.common.ANIMATE_Y_TIME
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.common.MAX_LIST_ITEMS
import com.example.gagyeboost.databinding.FragmentStatisticsBinding
import com.example.gagyeboost.model.data.StatRecordItem
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.NumberPickerDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import org.koin.androidx.viewmodel.ext.android.viewModel


class StatisticsFragment :
    BaseFragment<FragmentStatisticsBinding>(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModel()
    private lateinit var dialog: NumberPickerDialog
    private val statResultAdapter = StatResultAdapter()
    private lateinit var chartDaily: BarChart
    private lateinit var chartMonthly: PieChart

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setListeners()
        setObservers()
    }

    private fun initView() {
        dialog = NumberPickerDialog(binding.root.context)

        binding.viewModel = viewModel

        with(binding) {
            tvYearAndMonth.setOnClickListener {
                setDialog()
            }

            toggleGroupMoneyType.check(R.id.btn_expense)
            rvRecordList.adapter = statResultAdapter

            chartDaily = chartDailyStat
            chartMonthly = pieChartMonthStatistics
        }
    }

    private fun setObservers() {
        with(viewModel) {
            sortedStatRecordList.observe(viewLifecycleOwner, {
                binding.btnShowRecordList.isGone = it.size <= MAX_LIST_ITEMS
                when (viewModel.isShowingAllData.value) {
                    true -> statResultAdapter.submitList(it)
                    false -> {
                        if (it.size < MAX_LIST_ITEMS) {
                            statResultAdapter.submitList(it)
                        } else {
                            statResultAdapter.submitList(it.subList(0, MAX_LIST_ITEMS))
                        }
                    }
                }
                initPieChart(it)
            })

            selectedMoneyType.observe(viewLifecycleOwner) {
                viewModel.setDailyChartData()
                viewModel.loadRecordList()
            }

            yearMonthPair.observe(viewLifecycleOwner) {
                viewModel.loadRecordList()
                viewModel.setDailyChartData()
            }

            dailyChartData.observe(viewLifecycleOwner) {
                setDailyChart(it)
            }

            isShowingAllData.observe(viewLifecycleOwner) {
                when (it) {
                    true -> statResultAdapter.submitList(viewModel.sortedStatRecordList.value)
                    false -> statResultAdapter.submitList(
                        viewModel.sortedStatRecordList.value?.subList(
                            0,
                            5
                        )
                    )
                }
            }
        }
    }

    private fun setListeners() {
        binding.toggleGroupMoneyType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            viewModel.setSelectedMoneyType(if (checkedId == R.id.btn_expense) EXPENSE else INCOME)
        }

        binding.btnShowRecordList.setOnClickListener {
            viewModel.setDataListState()
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
            ).apply {
                colors = ColorTemplate.VORDIPLOM_COLORS.toList()
                setDrawValues(false)
            }

            val resultData = BarData(chartDataSet)

            val barWidth = resources.getDimensionPixelSize(R.dimen.chart_bar_width)
            val count = chartDataSet.entryCount.toFloat()
            val totalWidth = binding.chartDailyStat.width
            val ratio = barWidth * count / totalWidth
            resultData.barWidth = ratio

            data = resultData
            notifyDataSetChanged()
        }
    }

    private fun initPieChart(recordList: List<StatRecordItem>) {
        chartMonthly.apply {
            val colorList =
                ColorTemplate.MATERIAL_COLORS.toList() + ColorTemplate.COLORFUL_COLORS.toList()
            setUsePercentValues(true) // true : 백분율로 표시, false : 값으로 표시
            description.isEnabled = false
            setExtraOffsets(5f, 5f, 5f, 5f)
            legend.isEnabled = false

            isDragDecelerationEnabled = false // 드래그 시 마찰 계수 적용 여부
            dragDecelerationFrictionCoef = 0.95f // 드래그 시 마찰계수

            setHoleColor(Color.WHITE)
            setEntryLabelColor(Color.BLACK)

            animateY(ANIMATE_Y_TIME, Easing.EaseInOutCubic)
            val dataSet = PieDataSet(
                recordList.map {
                    PieEntry(
                        it.totalMoney.replace(",", "").toFloat(),
                        it.categoryIcon
                    )
                },
                ""
            ).apply {
                sliceSpace = 3f
                selectionShift = 5f
                xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
                colors = colorList

                valueLinePart1Length = 0.4f
                valueLinePart2Length = 0.3f
                valueLineWidth = 2f
                isUsingSliceColorAsValueLineColor = true
            }

            val data = PieData(dataSet).apply {
                setValueTextSize(12F)
                setValueTextColor(Color.WHITE)
                setEntryLabelTextSize(18F)
                setValueTypeface(Typeface.DEFAULT_BOLD)
                setValueFormatter(CustomPieNumberFormatter(chartMonthly))
            }

            setData(data)
        }
    }
}
