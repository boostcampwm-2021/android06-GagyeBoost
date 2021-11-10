package com.example.gagyeboost.ui.home.selectPosition

import android.location.Address
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemAddressBinding
import com.example.gagyeboost.ui.home.AddViewModel

class AddressAdapter(
    private val viewModel: AddViewModel,
    private val itemClickListener: () -> Unit
) : ListAdapter<Address, AddressAdapter.AddressViewHolder>(diffUtil) {

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
                viewModel.selectedAddress.value = getItem(adapterPosition)
                itemClickListener()

            }
        }

        fun bind(item: Address) {
            binding.tvAddress.text = item.getAddressLine(0)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Address>() {
            override fun areItemsTheSame(oldItem: Address, newItem: Address) =
                oldItem.featureName == newItem.featureName

            override fun areContentsTheSame(oldItem: Address, newItem: Address) =
                oldItem.equals(newItem)

        }
    }
}
