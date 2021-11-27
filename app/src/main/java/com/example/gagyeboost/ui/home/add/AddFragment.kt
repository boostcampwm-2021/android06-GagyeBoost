package com.example.gagyeboost.ui.home.add

import android.content.Context
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
import org.koin.androidx.navigation.koinNavGraphViewModel

class AddFragment : BaseFragment<FragmentAddBinding>(R.layout.fragment_add) {

    private val viewModel by koinNavGraphViewModel<AddViewModel>(R.id.addMoneyGraph)
    private lateinit var inputMethodManager: InputMethodManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initClickListeners()
        setTextSize()
        editTextFocus()
    }

    private fun initView() {
        binding.viewModel = viewModel
        val dateStr = arguments?.getString(TODAY_STRING_KEY)
        binding.tvDate.text = dateStr
        viewModel.dateString = dateStr ?: ""
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private fun editTextFocus() {
        binding.etWon.requestFocus()
        inputMethodManager.showSoftInput(binding.etWon, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun setTextSize() {
        binding.etWon.setEditTextSize(binding.tvTextSize)
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

    override fun onPause() {
        super.onPause()
        inputMethodManager.hideSoftInputFromWindow(binding.etWon.windowToken, 0)
    }
}
