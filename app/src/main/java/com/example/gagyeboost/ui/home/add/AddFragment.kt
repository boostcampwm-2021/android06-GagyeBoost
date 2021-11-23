package com.example.gagyeboost.ui.home.add

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.bundleOf
import androidx.core.widget.doAfterTextChanged
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.IS_EXPENSE_KEY
import com.example.gagyeboost.common.TODAY_STRING_KEY
import com.example.gagyeboost.common.setEditTextSize
import com.example.gagyeboost.databinding.FragmentAddBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class AddFragment : BaseFragment<FragmentAddBinding>(R.layout.fragment_add) {

    private val viewModel by sharedViewModel<AddViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClickListeners()
        observe()
        setTextSize()
        editTextFocus()
    }

    private fun initView() {
        binding.viewModel = viewModel
        val dateStr = arguments?.getString(TODAY_STRING_KEY)
        binding.tvDate.text = dateStr
        viewModel.dateString = dateStr ?: ""
    }

    private fun editTextFocus() {
        binding.etWon.requestFocus()
        val imm = activity?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.etWon, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setTextSize() {
        binding.etWon.setEditTextSize(binding.tvTextSize)
    }

    private fun observe() {
        viewModel.money.observe(viewLifecycleOwner) {
            if (it == 0) {
                binding.btnExpense.isEnabled = false
                binding.btnIncome.isEnabled = false
            } else {
                binding.btnExpense.isEnabled = true
                binding.btnIncome.isEnabled = true
            }
        }
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
            viewModel.resetAllData()
        }

        binding.etWon.doAfterTextChanged {
            it?.let {
                if (it.length == 1) binding.etWon.setSelection(it.length)
            }
        }
    }

    private fun goToCategoryFragment(isExpense: Boolean) {
        findNavController().navigate(
            R.id.action_addFragment_to_categoryFragment,
            bundleOf(IS_EXPENSE_KEY to isExpense)
        )
    }
}
