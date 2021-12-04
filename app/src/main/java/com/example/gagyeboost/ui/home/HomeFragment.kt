package com.example.gagyeboost.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
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
import timber.log.Timber
import java.util.*

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by sharedViewModel()
    private lateinit var calendarAdapter: CalendarViewPagerAdapter
    private lateinit var dialog: NumberPickerDialog
    private lateinit var detailAdapter: DateDetailAdapter
    private val filterActivityLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            refreshCalendarData()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        calendarAdapter = CalendarViewPagerAdapter(requireParentFragment())

        setFragmentResultListener(SELECTED_DATE_KEY) { key, bundle ->
            val result = bundle.getLong(key)
            homeViewModel.setSelectedDate(longToCustomDate(result))
        }

        setFragmentResultListener(YEAR_MONTH) { key, bundle ->
            val result = bundle.get(key) as Date
            val date = longToCustomDate(result.time)
            homeViewModel.setYearAndMonth(date.year, date.month)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        clickListener()
        observe()

        binding.vpCalendar.adapter = calendarAdapter
        binding.vpCalendar.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.vpCalendar.setCurrentItem(calendarAdapter.FIRST_POSITION, false)

    }

    private fun initView() {
        binding.homeViewModel = homeViewModel
        detailAdapter = DateDetailAdapter { detailItemOnClick(it) }
        dialog = NumberPickerDialog(binding.root.context)

        with(binding) {
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
            val month =
                (dialog.binding.npYear.value - NOW_YEAR) * 12 + (dialog.binding.npMonth.value - NOW_MONTH)
            binding.vpCalendar.setCurrentItem(calendarAdapter.FIRST_POSITION + month, false)
            dialog.dismiss()
        }
    }

    private fun observe() {
        homeViewModel.detailItemList.observe(viewLifecycleOwner) {
            detailAdapter.submitList(it)
        }
    }

    private fun refreshCalendarData() {
//        homeViewModel.loadAllDayDataInMonth()
    }

    override fun onStop() {
        super.onStop()
        dialog.dismiss()
    }
}
