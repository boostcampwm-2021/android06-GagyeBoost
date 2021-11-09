package com.example.gagyeboost.ui.statstics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemStatRecordBinding
import com.example.gagyeboost.model.data.StatRecordItem

class StatResultAdapter : ListAdapter<StatRecordItem, StatResultAdapter.ResultRecordViewHolder>(
    StatResultAdapter.diffUtil
) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StatResultAdapter.ResultRecordViewHolder {
        val binding =
            ItemStatRecordBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ResultRecordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ResultRecordViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ResultRecordViewHolder(private val binding: ItemStatRecordBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(statRecordItem: StatRecordItem) {

        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<StatRecordItem>() {
            override fun areItemsTheSame(
                oldItem: StatRecordItem,
                newItem: StatRecordItem
            ): Boolean {
                // TODO("추후 로직 추가 예정")
                return true
            }

            override fun areContentsTheSame(
                oldItem: StatRecordItem,
                newItem: StatRecordItem
            ): Boolean {
                // TODO("추후 로직 추가 예정")
                return true
            }

        }
    }
}