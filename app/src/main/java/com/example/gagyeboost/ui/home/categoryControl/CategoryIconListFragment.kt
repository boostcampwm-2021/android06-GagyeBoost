package com.example.gagyeboost.ui.home.categoryControl

import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCategoryIconListBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.base.BaseFragment

class CategoryIconListFragment :
    BaseFragment<FragmentCategoryIconListBinding>(R.layout.fragment_category_icon_list) {
    private val categoryIconAdapter = CategoryIconAdapter {
        //TODO ViewModel에 icon data 변경
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvIconList.adapter = categoryIconAdapter
        categoryIconAdapter.submitList(testData)
    }
}

private val testData = listOf(
    Category(id = 1, categoryName = "식비", emoji = "\uD83C\uDF5A"),
    Category(id = 2, categoryName = "여가", emoji = "\uD83C\uDFBE"),
    Category(id = 3, categoryName = "교통", emoji = "\uD83D\uDE8C"),
    Category(id = 4, categoryName = "식비", emoji = "\uD83C\uDF5A"),
    Category(id = 5, categoryName = "여가", emoji = "\uD83C\uDFBE"),
    Category(id = 6, categoryName = "교통", emoji = "\uD83D\uDE8C"),
    Category(id = 7, categoryName = "식비", emoji = "\uD83C\uDF5A"),
    Category(id = 8, categoryName = "여가", emoji = "\uD83C\uDFBE"),
    Category(id = 9, categoryName = "교통", emoji = "\uD83D\uDE8C"),
    Category(id = 10, categoryName = "식비", emoji = "\uD83C\uDF5A"),
    Category(id = 12, categoryName = "여가", emoji = "\uD83C\uDFBE"),
    Category(id = 13, categoryName = "교통", emoji = "\uD83D\uDE8C")
)