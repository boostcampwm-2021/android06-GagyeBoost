package com.example.gagyeboost.ui.calendar

import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCalendarBinding
import com.example.gagyeboost.ui.base.BaseFragment

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {

    var pageIndex = 0
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.tvIndex.text = pageIndex.toString()
    }
}