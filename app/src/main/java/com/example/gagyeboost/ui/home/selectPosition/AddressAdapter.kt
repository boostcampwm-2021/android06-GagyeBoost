package com.example.gagyeboost.ui.home.selectPosition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemAddressBinding
import com.example.gagyeboost.model.data.PlaceDetail

class AddressAdapter(private val itemClickListener: (PlaceDetail) -> Unit) :
    PagingDataAdapter<PlaceDetail, AddressAdapter.AddressViewHolder>(diffCallback) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AddressViewHolder {
        return AddressViewHolder(
            ItemAddressBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                getItem(bindingAdapterPosition)?.let { item ->
                    itemClickListener(item)
                }
            }
        }

        fun bind(item: PlaceDetail?) {
            binding.item = item
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<PlaceDetail>() {
            override fun areItemsTheSame(oldItem: PlaceDetail, newItem: PlaceDetail) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: PlaceDetail, newItem: PlaceDetail) =
                oldItem == newItem
        }
    }
}
