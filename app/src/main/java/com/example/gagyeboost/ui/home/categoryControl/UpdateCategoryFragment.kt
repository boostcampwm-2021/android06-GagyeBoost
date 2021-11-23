package com.example.gagyeboost.ui.home.categoryControl

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentUpdateCategoryBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpdateCategoryFragment :
    BaseFragment<FragmentUpdateCategoryBinding>(R.layout.fragment_update_category) {
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController
    private lateinit var inputMethodManager: InputMethodManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.viewModel = viewModel
        with(binding.appBarUpdateCategory) {
            setNavigationOnClickListener {
                viewModel.resetSelectedCategory()
                navController.popBackStack()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        // TODO: 카테고리 삭제 로직 추가
                        true
                    }
                    else -> false
                }
            }
        }

        binding.tvIconBody.setOnClickListener {
            navController.navigate(R.id.action_updateCategoryFragment_to_categoryIconListFragment)
        }

        binding.btnUpdateCategoryComplete.setOnClickListener {
            if (binding.etNameBody.text.isEmpty()) {
                Toast.makeText(requireContext(), "이름을 반드시 입력해야 합니다", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateCategory()
                navController.popBackStack()
            }
        }

        binding.root.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(binding.etNameBody.windowToken, 0)
        }
    }

    override fun onPause() {
        super.onPause()
        inputMethodManager.hideSoftInputFromWindow(binding.etNameBody.windowToken, 0)
    }
}
