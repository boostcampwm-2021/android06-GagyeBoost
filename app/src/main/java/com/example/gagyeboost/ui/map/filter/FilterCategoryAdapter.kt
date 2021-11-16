package com.example.gagyeboost.ui.map.filter

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ItemRvFilterCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.map.MapViewModel

class FilterCategoryAdapter(private val viewModel: MapViewModel) :
    ListAdapter<Category, FilterCategoryAdapter.CategoryViewHolder>(diffUtil) {

    val categoryList = viewModel.categoryIDList.value ?: mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding =
            ItemRvFilterCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CategoryViewHolder(private val binding: ItemRvFilterCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private var currentItem: Category? = null

        init {
            binding.cbCategory.setOnCheckedChangeListener { buttonView, isChecked ->
                currentItem?.let {
                    if (isChecked) {
                        categoryList.add(it.id)
                    } else {
                        categoryList.remove(it.id)
                    }
                    viewModel.categoryIDList.value = categoryList
                }
            }
        }

        fun bind(categoryItem: Category) {
            currentItem = categoryItem
            binding.category = categoryItem
            setCategoryColor()
            binding.cbCategory.isChecked = categoryList.contains(categoryItem.id)
        }

        private fun setCategoryColor() {
            with(this.itemView.context) {
                val emojiBackground = binding.tvEmoji.background as GradientDrawable
                val nameBackground = binding.tvCategoryName.background as GradientDrawable
                val colorId: Int = when (adapterPosition % 10) {
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
