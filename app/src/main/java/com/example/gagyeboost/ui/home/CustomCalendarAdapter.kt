package com.example.gagyeboost.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemDateBinding
import com.example.gagyeboost.model.data.DateItem
import java.text.DecimalFormat
import java.util.*

class CustomCalendarAdapter(
    val viewModel: HomeViewModel
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
                if (getItem(adapterPosition).date > 0) {
                    selectedDatePosition = adapterPosition
                    viewModel.setSelectedDate(getItem(adapterPosition))
                    notifyDataSetChanged()
                }
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

            setMoney(binding.tvIncome, dateItem.income)
            setMoney(binding.tvExpense, dateItem.expense)
            // setClickedDate()
            setToday(dateItem)
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
                    selectedDatePosition != adapterPosition
                ) {
                    tvDate.setTextColor(Color.RED)
                    itemView.setBackgroundColor(Color.parseColor("#e6e6e6"))
                }
            }
        }

//        private fun setClickedDate() {
//            val context = binding.root.context
//            if (selectedDatePosition == adapterPosition) {
//                binding.root.setBackgroundColor(ContextCompat.getColor(context, R.color.light_green))
////                binding.tvIncome.setTextColor(ContextCompat.getColor(context, R.color.white))
////                binding.tvExpense.setTextColor(ContextCompat.getColor(context, R.color.white))
//            } else {
//                binding.root.background =
//                    ContextCompat.getDrawable(context, (R.color.white))
////                binding.tvIncome.setTextColor(ContextCompat.getColor(context, R.color.income))
////                binding.tvExpense.setTextColor(ContextCompat.getColor(context, R.color.expense))
//            }
//        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DateItem>() {
            override fun areItemsTheSame(oldItem: DateItem, newItem: DateItem) =
                oldItem.date == newItem.date

            override fun areContentsTheSame(oldItem: DateItem, newItem: DateItem) =
                oldItem == newItem
        }
    }
}
