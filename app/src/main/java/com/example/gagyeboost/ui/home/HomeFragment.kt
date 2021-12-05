package com.example.gagyeboost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.gagyeboost.R
import com.example.gagyeboost.common.*
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.calendar.CalendarViewPagerAdapter
import com.example.gagyeboost.ui.home.detail.DateDetailAdapter
import com.example.gagyeboost.ui.home.detail.RecordDetailActivity
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var calendarAdapter: CalendarViewPagerAdapter
    private lateinit var dialog: NumberPickerDialog
    private lateinit var detailAdapter: DateDetailAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(SELECTED_DATE_KEY) { key, bundle ->
            val result = bundle.getLong(key)
            homeViewModel.setSelectedDate(longToCustomDate(result))
        }

        setFragmentResultListener(YEAR_MONTH) { key, bundle ->
            val result = bundle.get(key) as Date
            val date = longToCustomDate(result.time)
            homeViewModel.setYearAndMonth(date.year, date.month)
            homeViewModel.selectedMonthMinusNow(date.year, date.month)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickListener()
        observe()
    }

    private fun initView() {
        detailAdapter = DateDetailAdapter { detailItemOnClick(it) }
        dialog = NumberPickerDialog(binding.root.context)
        calendarAdapter = CalendarViewPagerAdapter(requireParentFragment())

        with(binding) {
            viewModel = homeViewModel
            rvDetail.adapter = detailAdapter
            vpCalendar.adapter = calendarAdapter
            vpCalendar.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            vpCalendar.setCurrentItem(homeViewModel.viewPagerPosition, false)
        }
        homeViewModel.loadTotalMoney()
    }

    private fun detailItemOnClick(id: Int) {
        val intent = Intent(activity, RecordDetailActivity::class.java)
        intent.putExtra(DATE_DETAIL_ITEM_ID_KEY, id)
        startActivity(intent)
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
            val month = homeViewModel.selectedMonthMinusNow(
                dialog.binding.npYear.value,
                dialog.binding.npMonth.value
            )
            binding.vpCalendar.setCurrentItem(INIT_POSITION + month, false)
            dialog.dismiss()
        }
    }

    private fun observe() {
        homeViewModel.detailItemList.observe(viewLifecycleOwner) {
            detailAdapter.submitList(it)
        }
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.setSelectedDate(null)
    }
}
