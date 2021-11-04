package com.example.gagyeboost.ui.category

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
        initClickListeners()
        setObservers()
    }

    private fun initView() {
        binding.tvMoney.text =
            viewModel.getFormattedMoneyText(viewModel.money.value?.toIntOrNull() ?: 0)

        categoryAdapter = CategoryAdapter(
            {
                if (it < 0) {
                    // TODO: 카테고리 추가 화면으로 이동
                } else {
                    // TODO: 지도 선택 화면으로 이동
                }
                return@CategoryAdapter true
            }, {
                // TODO: 카테고리 수정 화면으로 이동(category id 값 넘겨주기)
                return@CategoryAdapter true
            })

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
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
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
