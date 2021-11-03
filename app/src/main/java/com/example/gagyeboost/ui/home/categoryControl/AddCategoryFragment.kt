package com.example.gagyeboost.ui.home.categoryControl

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentAddCategoryBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddCategoryFragment :
    BaseFragment<FragmentAddCategoryBinding>(R.layout.fragment_add_category) {
    private val viewModel by sharedViewModel<MainViewModel>()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        binding.viewModel = viewModel
        binding.btnAddCategoryBack.setOnClickListener {
            viewModel.selectedCategoryReset()
            navController.popBackStack()
        }
        binding.tvIconBody.setOnClickListener {
            navController.navigate(R.id.action_addCategoryFragment_to_categoryIconListFragment)
        }
        binding.btnAddCategoryComplete.setOnClickListener {
            if (binding.etNameBody.text.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 반드시 입력해야 합니다", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.addCategory()
                viewModel.selectedCategoryReset()
                navController.popBackStack()
            }
        }
    }

}