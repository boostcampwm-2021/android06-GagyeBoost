package com.example.gagyeboost.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemRvDetailBinding
import com.example.gagyeboost.model.data.DateDetailItem

class DateDetailAdapter :
    ListAdapter<DateDetailItem, DateDetailAdapter.DetailViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailViewHolder {
        return DetailViewHolder(
            ItemRvDetailBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: DetailViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class DetailViewHolder(private val binding: ItemRvDetailBinding) :
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
