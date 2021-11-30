package com.example.gagyeboost.ui.home.category

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ItemCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.home.AddViewModel

class CategoryAdapter(
    private val categoryClickListener: (Category) -> Boolean,
    private val categoryEditClickListener: (Category) -> Boolean,
    private val viewModel: ViewModel
) : ListAdapter<Category, CategoryAdapter.CategoryViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        binding.categoryClickListener = categoryClickListener
        binding.categoryEditClickListener = categoryEditClickListener
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(categoryItem: Category) {
            binding.category = categoryItem
            if (viewModel is AddViewModel) {
                binding.viewModel = viewModel

                (binding.viewModel as AddViewModel).isEdit.value?.let {
                    binding.ivSetting.isVisible = it
                }
            }
            setCategoryColor()
        }

        private fun setCategoryColor() {
            with(this.itemView.context) {
                val emojiBackground = binding.tvEmoji.background as GradientDrawable
                val nameBackground = binding.tvCategoryName.background as GradientDrawable
                val colorId: Int = when (bindingAdapterPosition % 10) {
                    0 -> ContextCompat.getColor(this, R.color.category1)
                    1 -> ContextCompat.getColor(this, R.color.category2)
                    2 -> ContextCompat.getColor(this, R.color.category3)
                    3 -> ContextCompat.getColor(this, R.color.category4)
                    4 -> ContextCompat.getColor(this, R.color.category5)
                    5 -> ContextCompat.getColor(this, R.color.category6)
                    6 -> ContextCompat.getColor(this, R.color.category7)
                    7 -> ContextCompat.getColor(this, R.color.category8)
                    8 -> ContextCompat.getColor(this, R.color.category9)
                    9 -> ContextCompat.getColor(this, R.color.category10)
                    else -> ContextCompat.getColor(this, R.color.expense)
                }
                emojiBackground.setStroke(3, colorId)
                emojiBackground.setColor(colorId)
                nameBackground.setStroke(3, colorId)
            }
        }
    }

    companion object {

        val diffUtil = object : DiffUtil.ItemCallback<Category>() {

            override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
                return oldItem == newItem
            }
        }
    }

}
