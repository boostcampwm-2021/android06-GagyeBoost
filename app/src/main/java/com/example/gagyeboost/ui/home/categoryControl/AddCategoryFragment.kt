package com.example.gagyeboost.ui.home.categoryControl

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentAddCategoryBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddCategoryFragment :
    BaseFragment<FragmentAddCategoryBinding>(R.layout.fragment_add_category) {
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        binding.viewModel = viewModel

        binding.appBarAddCategory.setNavigationOnClickListener {
            viewModel.resetSelectedCategory()
            navController.popBackStack()
        }

        binding.tvIconBody.setOnClickListener {
            navController.navigate(R.id.action_addCategoryFragment_to_categoryIconListFragment)
        }

        binding.btnAddCategoryComplete.setOnClickListener {
            if (binding.etNameBody.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.must_enter_category_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.addCategory()
                navController.popBackStack()
            }
        }
    }

}