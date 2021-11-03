package com.example.gagyeboost.ui.category

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    private val categoryAdapter = CategoryAdapter()
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickListeners()
        setObservers()
    }

    private fun initView() {
        viewModel.loadCategoryList()
        binding.tvMoney.text =
            viewModel.getFormattedMoneyText(viewModel.money.value?.toIntOrNull() ?: 0)

        with(binding) {
            viewModel = viewModel

            rvCategory.adapter = categoryAdapter

            arguments?.let {
                if (it.getBoolean(IS_EXPENSE_KEY)) tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.income
                    )
                ) else {
                    tvMoney.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.expense
                        )
                    )
                }
            }
        }
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }

    private fun setObservers() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }
    }
}
