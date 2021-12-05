package com.example.gagyeboost.ui.home.calendar

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.example.gagyeboost.R
import com.example.gagyeboost.common.ADD_MONTH_DATA
import com.example.gagyeboost.common.SELECTED_DATE_KEY
import com.example.gagyeboost.common.YEAR_MONTH
import com.example.gagyeboost.common.dateToLong
import com.example.gagyeboost.databinding.FragmentCalendarBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

class CalendarFragment(private val pageIndex: Int) :
    BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    private val viewModel: CalendarViewModel by viewModel()
    private val customCalendarAdapter by lazy { CustomCalendarAdapter(viewModel) }
    private var year: Int = 0
    private var month: Int = 0
    private val date = Calendar.getInstance().run {
        add(Calendar.MONTH, pageIndex)
        time
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener(ADD_MONTH_DATA) { _, _ ->
            viewModel.loadAllDayDataInMonth()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        year = SimpleDateFormat("yyyy", Locale.KOREA).format(date.time).toInt()
        month = SimpleDateFormat("MM", Locale.KOREA).format(date.time).toInt()

        binding.rvCalendar.adapter = customCalendarAdapter

        viewModel.dateItemList.observe(viewLifecycleOwner) {
            customCalendarAdapter.submitList(it)
        }

        viewModel.selectedDate.observe(viewLifecycleOwner) { date ->
            val selectedData = date?.let { dateToLong(it.year, it.month, it.date) }
            setFragmentResult(SELECTED_DATE_KEY, bundleOf(SELECTED_DATE_KEY to selectedData))
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setYearAndMonth(year, month)
        setFragmentResult(YEAR_MONTH, bundleOf(YEAR_MONTH to date))
    }
}
