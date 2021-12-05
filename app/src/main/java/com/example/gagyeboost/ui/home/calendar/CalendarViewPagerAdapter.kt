package com.example.gagyeboost.ui.home.calendar

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.gagyeboost.common.INIT_POSITION

class CalendarViewPagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {

    private val pageCount = Int.MAX_VALUE

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        return CalendarFragment(position - (INIT_POSITION))
    }
}
