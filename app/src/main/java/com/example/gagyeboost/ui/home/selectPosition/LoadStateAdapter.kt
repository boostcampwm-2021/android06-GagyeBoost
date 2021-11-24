package com.example.gagyeboost.ui.home.selectPosition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ItemAddressFooterBinding

class LoadStateAdapter : LoadStateAdapter<AddressFooterViewHolder>() {

    override fun onBindViewHolder(holder: AddressFooterViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): AddressFooterViewHolder {
        return AddressFooterViewHolder.create(parent)
    }
}

class AddressFooterViewHolder(private val binding: ItemAddressFooterBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(loadState: LoadState) {
        binding.pbLoading.isVisible = loadState is LoadState.Loading
    }

    companion object {
        fun create(parent: ViewGroup): AddressFooterViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_address_footer, parent, false)
            val binding = ItemAddressFooterBinding.bind(view)
            return AddressFooterViewHolder(binding)
        }
    }
}