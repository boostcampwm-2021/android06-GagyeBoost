package com.example.gagyeboost.ui.category

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentCategoryBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class CategoryFragment : BaseFragment<FragmentCategoryBinding>(R.layout.fragment_category) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        binding.tvMoney.text = viewModel.getFormattedMoneyText(viewModel.money.value?.toInt() ?: 0)

        arguments?.let {
            if (it.getBoolean("isExpense")) binding.tvMoney.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.income
                )
            ) else {
                binding.tvMoney.setTextColor(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.expense
                    )
                )
            }
        }

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnClose.setOnClickListener {
            findNavController().navigate(R.id.action_categoryFragment_to_homeFragment)
        }
    }
}
