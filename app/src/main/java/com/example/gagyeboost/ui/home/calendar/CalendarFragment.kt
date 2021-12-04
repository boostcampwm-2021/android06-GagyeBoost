package com.example.gagyeboost.ui.home.calendar

import android.os.Bundle
import android.view.View
import androidx.fragment.app.setFragmentResultListener
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCalendarBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment(val pageIndex: Int) : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModel()
    private val customCalendarAdapter by lazy { CustomCalendarAdapter(viewModel) }
    private var year: Int = 0
    private var month: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val date = Calendar.getInstance().run {
            add(Calendar.MONTH, pageIndex)
            time
        }

        year = SimpleDateFormat("yyyy", Locale.KOREA).format(date.time).toInt()
        month = SimpleDateFormat("MM", Locale.KOREA).format(date.time).toInt()
        Timber.e("page: $pageIndex")
        Timber.e("year: $year, month: $month ")

        viewModel.setYearAndMonth(year, month)
        binding.rvCalendar.adapter = customCalendarAdapter

        binding.tvYear.text = "$year $month"

        viewModel.dateItemList.observe(viewLifecycleOwner) {
            customCalendarAdapter.submitList(it)
        }
    }

    override fun onResume() {
        super.onResume()

        Timber.e("Calendar Fragment onResume $pageIndex : $year $month")
    }

    override fun onPause() {
        super.onPause()
        viewModel.selectedDatePosition?.let {
            customCalendarAdapter.notifyItemChanged(it)
            viewModel.setSelectedDate(null)
        }
        Timber.e("Calendar Fragment onPause $pageIndex : $year $month")
    }
}
