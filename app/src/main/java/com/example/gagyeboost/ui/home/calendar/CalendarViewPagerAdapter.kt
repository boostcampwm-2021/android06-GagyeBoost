package com.example.gagyeboost.ui.home.calendar

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class CalendarViewPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

    private val pageCount = Int.MAX_VALUE
    val FIRST_POSITION = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        return CalendarFragment(position - (FIRST_POSITION))
    }
}
