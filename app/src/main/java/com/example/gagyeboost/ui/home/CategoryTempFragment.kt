package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCategoryTempBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryTempFragment
    : BaseFragment<FragmentCategoryTempBinding>(R.layout.fragment_category_temp){
    private val categoryAdapter = CategoryAdapter()
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        viewModel.loadCategoryList()
        setObservers()
    }

    private fun initView() {
        binding.rvCategory.adapter = categoryAdapter
    }

    private fun setObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }
    }
}