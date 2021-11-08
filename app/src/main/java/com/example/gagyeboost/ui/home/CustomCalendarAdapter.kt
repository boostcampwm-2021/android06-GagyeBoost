package com.example.gagyeboost.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ItemDateBinding
import com.example.gagyeboost.model.data.DateItem
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
        private val cal = Calendar.getInstance()
        private val currentYear = cal.get(Calendar.YEAR)
        private val currentMonth = cal.get(Calendar.MONTH) + 1
        private val currentDate = cal.get(Calendar.DATE)

        init {
            itemView.setOnClickListener {
                notifyItemChanged(adapterPosition)
                itemClickListener(getItem(adapterPosition))
                selectedDatePosition?.let { notifyItemChanged(it) }
                selectedDatePosition = adapterPosition

            }
        }

        fun bind(dateItem: DateItem) {
            binding.item = dateItem
            binding.viewModel = viewModel
            binding.tvDate.isVisible = true
            if (dateItem.date < 0) {
                binding.tvDate.isGone = true
            }
            binding.executePendingBindings()
            setClickedDate(dateItem)
            setToday(dateItem)
            setMoney(binding.tvIncome, dateItem.income)
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
                if (currentYear == dateItem.year &&
                    currentMonth == dateItem.month &&
                    currentDate == dateItem.date &&
                    viewModel!!.selectedDate.value != getItem(adapterPosition)
                ) {
                    tvDate.setTextColor(Color.RED)
                    itemView.setBackgroundColor(Color.parseColor("#e6e6e6"))
                }
            }
        }

        private fun setClickedDate(dateItem: DateItem) {
            binding.root.background =
                ContextCompat.getDrawable(
                    binding.root.context, if (viewModel.selectedDate.value == dateItem) {
                        R.color.light_green
                    } else {
                        R.color.white
                    }
                )

            binding.tvIncome.setTextColor(
                ContextCompat.getColor(
                    binding.tvIncome.context, if (viewModel.selectedDate.value == dateItem) {
                        R.color.white
                    } else {
                        R.color.income
                    }
                )
            )

            binding.tvExpense.setTextColor(
                ContextCompat.getColor(
                    binding.tvIncome.context, if (viewModel.selectedDate.value == dateItem) {
                        R.color.white
                    } else {
                        R.color.expense
                    }
                )
            )
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
