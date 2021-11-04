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
    val viewModel: HomeViewModel,
    private val itemClickListener: (DateItem) -> Unit
) : ListAdapter<DateItem, CustomCalendarAdapter.DateViewHolder>(diffUtil) {

    private var selectedDatePosition: Int? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val binding = ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DateViewHolder(binding)
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
        private val currentMonth = cal.get(Calendar.MONTH) + 1
        private val currentDate = cal.get(Calendar.DATE)

        init {
            itemView.setOnClickListener {
                currentItem?.let { item -> itemClickListener.invoke(item) }

                notifyItemChanged(adapterPosition)
                selectedDatePosition?.let { notifyItemChanged(it) }
                selectedDatePosition = adapterPosition
            }
        }

        fun bind(dateItem: DateItem) {
            currentItem = dateItem
            binding.item = dateItem
            binding.viewModel = viewModel
            binding.executePendingBindings()
            setToday(dateItem)
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

        private fun setToday(dateItem: DateItem) {
            with(binding) {
                if (currentYear == dateItem.year && currentMonth == dateItem.month && currentDate == dateItem.date) {
                    tvDate.setTextColor(Color.RED)
                    itemView.setBackgroundColor(Color.parseColor("#e6e6e6"))
                }
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
