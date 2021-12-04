package com.example.gagyeboost.ui.calendar

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class CalendarAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    private val pageCount = Int.MAX_VALUE
    val FIRST_POSITION = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        val calendarFragment = CalendarFragment()
        calendarFragment.pageIndex = position
        return calendarFragment
    }
}