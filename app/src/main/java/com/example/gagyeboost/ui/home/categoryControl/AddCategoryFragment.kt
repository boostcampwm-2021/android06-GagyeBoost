package com.example.gagyeboost.ui.home.categoryControl

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentAddCategoryBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class AddCategoryFragment :
    BaseFragment<FragmentAddCategoryBinding>(R.layout.fragment_add_category) {
    private val viewModel by koinNavGraphViewModel<AddViewModel>(R.id.addMoneyGraph)
    private lateinit var navController: NavController
    private lateinit var inputMethodManager: InputMethodManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        initView()
        setListeners()
    }

    private fun initView() {
        binding.viewModel = viewModel
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        with(binding) {
            etNameBody.requestFocus()
            inputMethodManager.showSoftInput(binding.etNameBody, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun setListeners() {
        with(binding) {
            appBarAddCategory.setNavigationOnClickListener {
                navController.popBackStack()
            }

            tvIconBody.setOnClickListener {
                navController.navigate(R.id.action_addCategoryFragment_to_categoryIconListFragment)
            }

            btnAddCategoryComplete.setOnClickListener {
                if (binding.etNameBody.text.isEmpty()) {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.must_enter_category_name),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    this@AddCategoryFragment.viewModel.addCategory()
                    navController.popBackStack()
                }
            }

            root.setOnClickListener {
                inputMethodManager.hideSoftInputFromWindow(etNameBody.windowToken, 0)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        inputMethodManager.hideSoftInputFromWindow(binding.etNameBody.windowToken, 0)
    }
}
