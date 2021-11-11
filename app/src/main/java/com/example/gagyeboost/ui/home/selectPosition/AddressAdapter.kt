package com.example.gagyeboost.ui.home.selectPosition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemAddressBinding
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.home.AddViewModel

class AddressAdapter(
    private val viewModel: AddViewModel,
    private val itemClickListener: () -> Unit
) : ListAdapter<PlaceDetail, AddressAdapter.AddressViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            itemView.setOnClickListener {
                itemClickListener()
                viewModel.selectedAddress.value = getItem(adapterPosition)
            }
        }

        fun bind(item: PlaceDetail) {
            binding.item = item
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PlaceDetail>() {
            override fun areItemsTheSame(oldItem: PlaceDetail, newItem: PlaceDetail) =
                oldItem.geometry == newItem.geometry

            override fun areContentsTheSame(oldItem: PlaceDetail, newItem: PlaceDetail) =
                oldItem == newItem

        }
    }
}
