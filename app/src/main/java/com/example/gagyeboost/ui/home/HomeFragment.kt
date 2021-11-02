package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()
    private val calendar = CustomCalendar()
    private val customCalendarAdapter = CustomCalendarAdapter(calendar) {
        Toast.makeText(requireContext(), it + "CLICKED", Toast.LENGTH_SHORT).show()
    }
    private val dateItemList = mutableListOf<DateItem>()

    // 임시로 customCalendarAdapter 데이터 submit
    init {
        calendar.datesInMonth.forEach {
            dateItemList.add(DateItem((0..10000).random(), (0..10000).random(), it, 2021, 10))
        }
    }


    private lateinit var dialog: NumberPickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setDialog()
    }

    private fun setDialog() {

        binding.tvYearAndMonth.setOnClickListener {
            viewModel.startDialog(dialog)

            dialog.setOnCancelListener {
                viewModel.setYearAndMonth(dialog.binding.npYear.value, dialog.binding.npMonth.value)
            }
            dialog.binding.tvAgree.setOnClickListener {
                viewModel.setYearAndMonth(dialog.binding.npYear.value, dialog.binding.npMonth.value)
                dialog.dismiss()
            }
            dialog.binding.tvCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
    }

    private fun initView() {
        binding.viewModel = viewModel
        dialog = NumberPickerDialog(binding.root.context)
        with(binding.rvCalendar) {
            adapter = customCalendarAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        customCalendarAdapter.submitList(dateItemList)
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
