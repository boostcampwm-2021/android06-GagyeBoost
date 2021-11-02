package com.example.gagyeboost.ui.home.categoryControl

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentAddCategoryBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddCategoryFragment :
    BaseFragment<FragmentAddCategoryBinding>(R.layout.fragment_add_category) {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        binding.tvIconBody.setOnClickListener {
            navController.navigate(R.id.action_addCategoryFragment_to_categoryIconListFragment)

        }
    }

}