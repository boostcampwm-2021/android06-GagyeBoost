package com.example.gagyeboost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.DATE_DETAIL_ITEM_ID_KEY
import com.example.gagyeboost.common.TODAY_STRING_KEY
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.detail.DateDetailAdapter
import com.example.gagyeboost.ui.home.detail.RecordDetailActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var customCalendarAdapter: CustomCalendarAdapter
    private lateinit var dialog: NumberPickerDialog
    private lateinit var detailAdapter: DateDetailAdapter
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshCalendarData()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        customCalendarAdapter = CustomCalendarAdapter(homeViewModel)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setDialog()
        observe()

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
        detailAdapter = DateDetailAdapter {
            val intent = Intent(activity, RecordDetailActivity::class.java)
            intent.putExtra(DATE_DETAIL_ITEM_ID_KEY, it)
            filterActivityLauncher.launch(intent)
            return@DateDetailAdapter true
        }

        with(binding) {
            dialog = NumberPickerDialog(root.context)
            rvCalendar.adapter = customCalendarAdapter
            rvDetail.adapter = detailAdapter
        }
    }

    private fun setDialog() {
        binding.tvYearAndMonth.setOnClickListener {
            dialog.window?.setGravity(Gravity.TOP)
            dialog.show()

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

        homeViewModel.selectedDate.observe(viewLifecycleOwner) {
            homeViewModel.loadDateDetailItemList(it)
        }

        homeViewModel.dateItemList.observe(viewLifecycleOwner) {
            customCalendarAdapter.submitList(it)
        }

        homeViewModel.detailItemList.observe(viewLifecycleOwner) {
            detailAdapter.submitList(it)
        }
    }

    private fun refreshCalendarData() {
        homeViewModel.loadAllDayDataInMonth()
        homeViewModel.loadDateDetailItemList(homeViewModel.selectedDate.value)
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
