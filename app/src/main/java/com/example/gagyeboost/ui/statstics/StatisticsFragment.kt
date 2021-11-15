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
                //TODO chart 표현하기

            })
            selectedType.observe(viewLifecycleOwner, {
                viewModel.loadRecordList()
            })
            yearMonthPair.observe(viewLifecycleOwner, {
                viewModel.loadRecordList()
            })

        }
    }
}
