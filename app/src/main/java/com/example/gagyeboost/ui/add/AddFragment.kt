package com.example.gagyeboost.ui.add

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.databinding.FragmentAddBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddFragment : BaseFragment<FragmentAddBinding>(R.layout.fragment_add) {

    private val viewModel by sharedViewModel<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewModel = viewModel

        initClickListeners()
    }

    private fun initClickListeners() {
        binding.btnIncome.setOnClickListener {
            goToCategoryFragment(true)
        }

        binding.btnExpense.setOnClickListener {
            goToCategoryFragment(false)
        }

        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun goToCategoryFragment(isExpense: Boolean) {
        findNavController().navigate(
            R.id.action_addFragment_to_categoryFragment,
            bundleOf(IS_EXPENSE_KEY to isExpense)
        )
    }
}
