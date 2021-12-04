package com.example.gagyeboost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.gagyeboost.R
import com.example.gagyeboost.common.DATE_DETAIL_ITEM_ID_KEY
import com.example.gagyeboost.common.TODAY_STRING_KEY
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.calendar.CalendarAdapter
import com.example.gagyeboost.ui.home.detail.DateDetailAdapter
import com.example.gagyeboost.ui.home.detail.RecordDetailActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private val customCalendarAdapter by lazy { CustomCalendarAdapter(homeViewModel) }
    private lateinit var dialog: NumberPickerDialog
    private lateinit var detailAdapter: DateDetailAdapter
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshCalendarData()
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickListener()
        observe()

        val calendarAdapter = CalendarAdapter(requireActivity())
        binding.vpCalendar.adapter = calendarAdapter
        binding.vpCalendar.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpCalendar.currentItem = calendarAdapter.FIRST_POSITION
    }

    private fun initView() {
        binding.homeViewModel = homeViewModel
        detailAdapter = DateDetailAdapter { detailItemOnClick(it) }
        dialog = NumberPickerDialog(binding.root.context)

        with(binding) {
            rvCalendar.adapter = customCalendarAdapter
            rvDetail.adapter = detailAdapter
        }
    }

    private fun detailItemOnClick(id: Int) {
        val intent = Intent(activity, RecordDetailActivity::class.java)
        intent.putExtra(DATE_DETAIL_ITEM_ID_KEY, id)
        filterActivityLauncher.launch(intent)
    }

    private fun clickListener() {
        binding.tvYearAndMonth.setOnClickListener {
            setDialog()
        }
        binding.fabAdd.setOnClickListener {
            val today = homeViewModel.getTodayString()
            findNavController().navigate(
                R.id.action_homeFragment_to_addMoneyGraph,
                bundleOf(TODAY_STRING_KEY to today)
            )
        }
    }

    private fun setDialog() {
        dialog.window?.setGravity(Gravity.TOP)
        dialog.show()

        dialog.binding.tvAgree.setOnClickListener {
            homeViewModel.setYearAndMonth(
                dialog.binding.npYear.value,
                dialog.binding.npMonth.value
            )
            dialog.dismiss()
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

    private fun refreshCalendarData() {
        homeViewModel.loadAllDayDataInMonth()
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
