package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCalendarBinding
import com.example.gagyeboost.ui.base.BaseFragment

class CalendarFragment : BaseFragment<FragmentCalendarBinding>(R.layout.fragment_calendar) {
    private val customCalendarAdapter = CustomCalendarAdapter {
        Toast.makeText(requireContext(), it + "CLICKED", LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
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
    }
}