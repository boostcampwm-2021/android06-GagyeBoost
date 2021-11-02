package com.example.gagyeboost.ui.home.categoryControl

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.databinding.ItemCategoryIconBinding
import com.example.gagyeboost.model.data.Category

class CategoryIconAdapter(
    private val categoryList: List<Category>,
    private val onClickListener: (Int) -> Unit
) : ListAdapter<Category, CategoryIconAdapter.ViewHolder>(CategoryDiffUtil()) {

    inner class ViewHolder(private val binding: ItemCategoryIconBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Category) {
            binding.tvIcon.text = data.emoji
            binding.tvIcon.setOnClickListener {
                onClickListener(data.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemCategoryIconBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categoryList[position])
    }
}

private class CategoryDiffUtil : DiffUtil.ItemCallback<Category>() {
    override fun areItemsTheSame(oldItem: Category, newItem: Category) = oldItem.id == newItem.id


    override fun areContentsTheSame(oldItem: Category, newItem: Category) = oldItem == newItem

}