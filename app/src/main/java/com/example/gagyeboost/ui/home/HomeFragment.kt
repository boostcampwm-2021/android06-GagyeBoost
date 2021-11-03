package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModel()
    private val viewModel by sharedViewModel<MainViewModel>()
    private val calendar = CustomCalendar()
    private val customCalendarAdapter by lazy {
        CustomCalendarAdapter(calendar, viewModel) {
            Toast.makeText(requireContext(), it.date.toString() + "CLICKED", Toast.LENGTH_SHORT)
                .show()

            viewModel.selectedDate = it
        }
    }
    private val dateItemList = mutableListOf<DateItem>()

    // 임시로 customCalendarAdapter 데이터 submit
    init {
        calendar.datesInMonth.forEach {
            dateItemList.add(DateItem(null, (0..10000).random(), it, 2021, 10))
        }
    }

    private lateinit var dialog: NumberPickerDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initView()
        setDialog()
    }

    private fun setDialog() {

        binding.tvYearAndMonth.setOnClickListener {
            homeViewModel.startDialog(dialog)

            dialog.setOnCancelListener {
                homeViewModel.setYearAndMonth(
                    dialog.binding.npYear.value,
                    dialog.binding.npMonth.value
                )
            }
            dialog.binding.tvAgree.setOnClickListener {
                homeViewModel.setYearAndMonth(
                    dialog.binding.npYear.value,
                    dialog.binding.npMonth.value
                )
                dialog.dismiss()
            }
            dialog.binding.tvCancel.setOnClickListener {
                dialog.dismiss()
            }
        }
        viewModel.getMonthIncome()
        viewModel.getMonthExpense()
        viewModel.setTotalMoney()
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

    private fun initView() {
        binding.homeViewModel = homeViewModel
        dialog = NumberPickerDialog(binding.root.context)
        with(binding.rvCalendar) {
            adapter = customCalendarAdapter
        }
        customCalendarAdapter.submitList(dateItemList)
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
