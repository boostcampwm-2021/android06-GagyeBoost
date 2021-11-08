package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.TODAY_STRING_KEY
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModel()
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var customCalendarAdapter: CustomCalendarAdapter
    private lateinit var dialog: NumberPickerDialog
    private val detailAdapter: DateDetailAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customCalendarAdapter = CustomCalendarAdapter(homeViewModel) {
            if (it.date > 0) homeViewModel.setSelectedDate(it)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initView()
        setDialog()
        observe()

        viewModel.loadMonthIncome()
        viewModel.loadMonthExpense()
        viewModel.setTotalMoney()
        binding.fabAdd.setOnClickListener {
            val today = homeViewModel.getTodayString()
            findNavController().navigate(
                R.id.action_homeFragment_to_addFragment,
                bundleOf(TODAY_STRING_KEY to today)
            )
        }
    }

    private fun initView() {
        binding.homeViewModel = homeViewModel
        dialog = NumberPickerDialog(binding.root.context)

        binding.rvCalendar.adapter = customCalendarAdapter

        binding.rvDetail.adapter = detailAdapter
    }

    private fun setDialog() {
        binding.tvYearAndMonth.setOnClickListener {
            dialog.window?.setGravity(Gravity.TOP)
            dialog.show()

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
    }

    private fun observe() {
        homeViewModel.yearMonthPair.observe(viewLifecycleOwner) {
            homeViewModel.loadAllDayDataInMonth()
        }

        homeViewModel.dateItemList.observe(viewLifecycleOwner) {
            customCalendarAdapter.submitList(it)
        }

        homeViewModel.detailItemList.observe(viewLifecycleOwner) {
            detailAdapter.submitList(it)
        }
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
