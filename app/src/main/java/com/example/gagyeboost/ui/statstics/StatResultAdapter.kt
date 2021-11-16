package com.example.gagyeboost.ui.statstics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemStatRecordBinding
import com.example.gagyeboost.model.data.StatRecordItem

class StatResultAdapter : ListAdapter<StatRecordItem, StatResultAdapter.ResultRecordViewHolder>(
    diffUtil
) {
    var isShowingAllList = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ResultRecordViewHolder {
        val binding =
            ItemStatRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemCount(): Int {
        val count = currentList.size
        if (isShowingAllList) return count
        return if (count > MAX_VISIBLE_ITEMS) MAX_VISIBLE_ITEMS else count
    }

    class ResultRecordViewHolder(private val binding: ItemStatRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(statRecordItem: StatRecordItem) {
            binding.statRecord = statRecordItem
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<StatRecordItem>() {
            override fun areItemsTheSame(
                oldItem: StatRecordItem,
                newItem: StatRecordItem
            ): Boolean {
                return oldItem.categoryId == newItem.categoryId
            }

            override fun areContentsTheSame(
                oldItem: StatRecordItem,
                newItem: StatRecordItem
            ): Boolean {
                return oldItem == newItem
            }
        }

        private const val MAX_VISIBLE_ITEMS = 5
    }
}
