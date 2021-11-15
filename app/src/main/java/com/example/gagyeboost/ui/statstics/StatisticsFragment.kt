package com.example.gagyeboost.ui.statstics

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.FragmentStatisticsBinding
import com.example.gagyeboost.model.data.StatRecordItem
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.NumberPickerDialog
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>(R.layout.fragment_statistics) {
    private val viewModel: StatisticsViewModel by viewModel()
    private lateinit var dialog: NumberPickerDialog
    private val statResultAdapter = StatResultAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
    }

    private fun initView() {
        dialog = NumberPickerDialog(binding.root.context)

        binding.viewModel = viewModel
        binding.tvYearAndMonth.setOnClickListener {
            setDialog()
        }
        binding.toggleGroupMoneyType.addOnButtonCheckedListener { group, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            viewModel.setType(if (checkedId == R.id.btn_expense) EXPENSE else INCOME)
        }
        binding.toggleGroupMoneyType.check(R.id.btn_expense)
        binding.rvRecordList.adapter = statResultAdapter
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

    private fun initObserver() {
        with(viewModel) {
            sortedStatRecordList.observe(viewLifecycleOwner, {
                statResultAdapter.submitList(it)
                initPieChart(it)
            })
            selectedType.observe(viewLifecycleOwner, {
                viewModel.loadRecordList()
            })
            yearMonthPair.observe(viewLifecycleOwner, {
                viewModel.loadRecordList()
            })

        }
    }

    private fun initPieChart(recordList: List<StatRecordItem>) {
        binding.pieChartMonthStatistics.apply {
            setUsePercentValues(true) // true : 백분율로 표시, false : 값으로 표시
            description.isEnabled = false
            setExtraOffsets(5f, 5f, 5f, 5f)

            isDragDecelerationEnabled = false // 드래그 시 마찰 계수 적용 여부
            dragDecelerationFrictionCoef = 0.95f // 드래그 시 마찰계수

            setHoleColor(Color.WHITE)

            animateY(1500, Easing.EaseInOutCubic)
            val dataSet = PieDataSet(
                recordList.map { PieEntry(it.totalMoney.toFloat(), it.categoryName) },
                ""
            ).apply {
                sliceSpace = 3f
                selectionShift = 5f
                colors = (ColorTemplate.JOYFUL_COLORS.toMutableList())
            }

            val data = PieData(dataSet).apply {
                setValueTextSize(12f)
                setValueTextColor(Color.BLACK)
            }

            setData(data)
        }
    }
}
