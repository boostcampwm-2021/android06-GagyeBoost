package com.example.gagyeboost.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemDateBinding
import java.text.DecimalFormat
import java.util.*

class CustomCalendarAdapter(
    val calendar: CustomCalendar,
    private val itemClickListener: (String) -> Unit
) : ListAdapter<DateItem, CustomCalendarAdapter.DateViewHolder>(diffUtil) {

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
        holder.bind(getItem(position))
    }

    inner class DateViewHolder(private val binding: ItemDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        val dec = DecimalFormat("#,###")
        private var currentItem: DateItem? = null
        private val cal = Calendar.getInstance()
        private val currentYear = cal.get(Calendar.YEAR)
        private val currentMonth = cal.get(Calendar.MONTH)
        private val currentDate = cal.get(Calendar.DATE)

        init {
            itemView.setOnClickListener {
                currentItem?.let { itemClickListener.invoke(it.date.toString()) }
            }
        }

        fun bind(dateItem: DateItem) {
            currentItem = dateItem
            setDate(dateItem)
            setMoney(binding.tvEarnings, dateItem.income)
            setMoney(binding.tvExpense, dateItem.expense)
        }

        private fun setMoney(textView: TextView, money: Int?) {
            if (money == null) {
                textView.isGone = true
            } else {
                textView.text = dec.format(money)
            }
        }

        private fun setDate(dateItem: DateItem) {
            with(binding) {

                when (adapterPosition % CustomCalendar.DAYS_OF_WEEK) {
                    0 -> tvDate.setTextColor(Color.parseColor("#D96D84"))
                    6 -> tvDate.setTextColor(Color.parseColor("#6395e6"))
                    else -> tvDate.setTextColor(Color.parseColor("#676d6e"))
                }

                if (adapterPosition < calendar.prevMonthTailOffset
                    || adapterPosition >= calendar.prevMonthTailOffset + calendar.currentMonthMaxDate
                ) {
                    tvDate.alpha = 0.3f
                } else {
                    tvDate.alpha = 1f
                }

                if (currentYear == dateItem.year && currentMonth == dateItem.month && currentDate == dateItem.date) {
                    tvDate.setTextColor(Color.RED)
                    itemView.setBackgroundColor(Color.parseColor("#e6e6e6"))
                } else {
                    tvDate.setTextColor(Color.parseColor("#676d6e"))
                    itemView.setBackgroundColor(Color.parseColor("#FFFFFF"))
                }
                tvDate.text = dateItem.date.toString()
            }
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DateItem>() {
            override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem) =
                oldItem.year == newItem.year && oldItem.month == newItem.month && oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem) =
                oldItem == newItem
        }

    }
}
