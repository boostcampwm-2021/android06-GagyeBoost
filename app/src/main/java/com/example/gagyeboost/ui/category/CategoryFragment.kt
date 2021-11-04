package com.example.gagyeboost.ui.category

import android.os.Bundle
import android.view.View
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {
    private lateinit var categoryAdapter: CategoryAdapter
    private val viewModel by sharedViewModel<HomeViewModel>()

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

        categoryAdapter = CategoryAdapter(
            {
                Toast.makeText(requireContext(), "clicked", LENGTH_SHORT).show()
                if (it < 0) {
                    // TODO: 카테고리 추가 화면으로 이동
                } else {
                    // TODO: 지도 선택 화면으로 이동
                }
                return@CategoryAdapter true
            }, {
                // TODO: 카테고리 수정 화면으로 이동(category id 값 넘겨주기)
                Toast.makeText(requireContext(), "$it long clicked", LENGTH_SHORT).show()
                return@CategoryAdapter true
            })

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
            val categoryList = it.toMutableList()
            categoryList.add(Category(-1, getString(R.string.add), "➕"))
            categoryAdapter.submitList(categoryList)
        }
    }
}
