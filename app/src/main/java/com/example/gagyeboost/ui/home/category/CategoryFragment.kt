package com.example.gagyeboost.ui.home.category

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel by koinNavGraphViewModel<AddViewModel>(R.id.addMoneyGraph)
    private lateinit var navController: NavController
    private lateinit var inputMethodManager: InputMethodManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        initView()
        initClickListeners()
        setObservers()
    }

    private fun initView() {
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        categoryAdapter = CategoryAdapter(
            { category -> categoryOnClick(category) },
            { category -> categoryLongClick(category) },
            viewModel
        )

        binding.viewModel = viewModel
        binding.rvCategory.adapter = categoryAdapter

        arguments?.let {
            if (it.getBoolean(IS_EXPENSE_KEY)) {
                binding.tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.income
                    )
                )
                viewModel.setCategoryType(INCOME)
            } else {
                binding.tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.expense
                    )
                )
                viewModel.setCategoryType(EXPENSE)
            }
        }

        viewModel.loadCategoryList()
    }

    private fun categoryOnClick(category: Category): Boolean {
        if (category.id < 0) {
            navController.navigate(R.id.action_categoryFragment_to_addCategoryFragment)
        } else {
            viewModel.setCategoryData(category)
            navController.navigate(R.id.action_categoryFragment_to_selectPositionFragment)
        }
        return true
    }

    private fun categoryLongClick(category: Category): Boolean {
        viewModel.setCategoryData(category)
        navController.navigate(R.id.action_categoryFragment_to_updateCategoryFragment)
        return true
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
            viewModel.resetCategoryFragmentData()
        }

        binding.btnEdit.setOnClickListener {
            viewModel.isEdit.value?.let {
                viewModel.doEdit(!it)
            }
        }

        binding.root.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(binding.etHistory.windowToken, 0)
        }
    }

    private fun setObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            val categoryList = it.toMutableList()

            categoryList.add(Category(-1, getString(R.string.add), "➕", viewModel.categoryType))
            categoryAdapter.submitList(categoryList)
        }

        viewModel.isEdit.observe(viewLifecycleOwner) {
            categoryAdapter.notifyItemRangeChanged(0, categoryAdapter.itemCount)
        }
    }

    override fun onResume() {
        super.onResume()
        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )
    }

    override fun onPause() {
        super.onPause()
        inputMethodManager.hideSoftInputFromWindow(binding.etHistory.windowToken, 0)
        viewModel.doEdit(false)
    }
}
