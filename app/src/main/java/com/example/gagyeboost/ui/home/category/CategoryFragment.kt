package com.example.gagyeboost.ui.home.category

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.home.AddViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController
    private val homeViewModel by sharedViewModel<HomeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)

        initView()
        initClickListeners()
        setObservers()
    }

    private fun initView() {
        binding.tvMoney.text =
            homeViewModel.getFormattedMoneyText(viewModel.money.value?.toIntOrNull() ?: 0)

        categoryAdapter = CategoryAdapter(
            {
                if (it.id < 0) {
                    navController.navigate(R.id.action_categoryFragment_to_addCategoryFragment)
                } else {
                    viewModel.setCategoryData(it)
                    navController.navigate(R.id.action_categoryFragment_to_selectPositionFragment)
                }
                return@CategoryAdapter true
            }, {
                viewModel.setCategoryData(it)
                navController.navigate(R.id.action_categoryFragment_to_updateCategoryFragment)
                return@CategoryAdapter true
            })

        binding.viewModel = viewModel
        binding.rvCategory.adapter = categoryAdapter
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
                viewModel.setCategoryType(1.toByte())
            } else {
                binding.tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.expense
                    )
                )
                viewModel.setCategoryType(0.toByte())
            }
        }

        viewModel.loadCategoryList()
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack(R.id.homeFragment, false)
        }
    }

    private fun setObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            val categoryList = it.toMutableList()

            categoryList.add(Category(-1, getString(R.string.add), "➕", viewModel.categoryType))
            categoryAdapter.submitList(categoryList)
        }
    }
}
