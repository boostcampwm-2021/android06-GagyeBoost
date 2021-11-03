package com.example.gagyeboost.ui.home.categoryControl

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCategoryIconListBinding
import com.example.gagyeboost.model.data.emojiList
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryIconListFragment :
    BaseFragment<FragmentCategoryIconListBinding>(R.layout.fragment_category_icon_list) {
    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var navController: NavController
    private val categoryIconAdapter = CategoryIconAdapter {
        viewModel.setSelectedIcon(it)
        navController.popBackStack()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        binding.rvIconList.adapter = categoryIconAdapter
        categoryIconAdapter.submitList(emojiList)
        binding.btnCategoryIconListBack.setOnClickListener {
            navController.popBackStack()
        }
    }
}