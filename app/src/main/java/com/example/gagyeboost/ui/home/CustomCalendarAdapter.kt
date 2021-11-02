package com.example.gagyeboost.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemDateBinding

class CustomCalendarAdapter(private val itemClickListener: (String) -> Unit) :
    RecyclerView.Adapter<CustomCalendarAdapter.DateViewHolder>() {
    private val calendar = CustomCalendar()
    private val dateItemList = mutableListOf<DateItem>()

    init {
        calendar.datesInMonth.forEach {
            dateItemList.add(DateItem((0..1000).random(), (0..1000).random(), it))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        return DateViewHolder(
            ItemDateBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val date = dateItemList[position].date.toString()
        val layoutParams = GridLayoutManager.LayoutParams(holder.itemView.layoutParams)
        layoutParams.height = layoutParams.width

        with(holder) {
            bind(dateItemList[position])
            itemView.setOnClickListener { itemClickListener.invoke(date) }
            itemView.requestLayout()
        }
    }

    override fun getItemCount() = dateItemList.size

    inner class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dateItem: DateItem) {
            with(binding) {
                setDate(dateItem.date.toString())

                when (dateItem.expense) {
                    0 -> tvExpense.isGone = true
                    else -> {
                        tvExpense.isVisible = true
                        tvExpense.text = dateItem.expense.toString()
                    }
                }
                when (dateItem.income) {
                    0 -> tvEarnings.isGone = true
                    else -> {
                        tvEarnings.isVisible = true
                        tvEarnings.text = dateItem.income.toString()
                    }
                }
            }
        }

        private fun setDate(date: String) {
            with(binding) {
                if (adapterPosition % CustomCalendar.DAYS_OF_WEEK == 0) {
                    tvDate.setTextColor(Color.parseColor("#D96D84"))
                } else {
                    tvDate.setTextColor(Color.parseColor("#676d6e"))
                }

                if (adapterPosition < calendar.prevMonthTailOffset
                    || adapterPosition >= calendar.prevMonthTailOffset + calendar.currentMonthMaxDate) {
                    tvDate.alpha = 0.3f
                } else {
                    tvDate.alpha = 1f
                }

                tvDate.text = date
            }
        }
    }
}
