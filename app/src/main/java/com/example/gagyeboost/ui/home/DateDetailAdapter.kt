package com.example.gagyeboost.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemRvDetailBinding
import com.example.gagyeboost.model.data.DateDetailItem

class DateDetailAdapter(private val itemLongClickListener: (Int) -> Boolean) :
    ListAdapter<DateDetailItem, DateDetailAdapter.DetailViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        val binding =
            ItemRvDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.listener = itemLongClickListener

        return DetailViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class DetailViewHolder(private val binding: ItemRvDetailBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: DateDetailItem) {
            binding.item = item
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DateDetailItem>() {
            override fun areContentsTheSame(oldItem: DateDetailItem, newItem: DateDetailItem) =
                newItem == oldItem

            override fun areItemsTheSame(oldItem: DateDetailItem, newItem: DateDetailItem) =
                newItem.id == oldItem.id
        }
    }

}
