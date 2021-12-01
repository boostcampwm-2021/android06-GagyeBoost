package com.example.gagyeboost.ui.home.categoryControl

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentUpdateCategoryBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.navigation.koinNavGraphViewModel

class UpdateCategoryFragment :
    BaseFragment<FragmentUpdateCategoryBinding>(R.layout.fragment_update_category) {

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
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.viewModel = viewModel

        with(binding) {
            etNameBody.requestFocus()
            inputMethodManager.showSoftInput(binding.etNameBody, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun setListeners() {
        with(binding) {
            appBarUpdateCategory.setNavigationOnClickListener {
                this@UpdateCategoryFragment.viewModel.resetSelectedCategory()
                navController.popBackStack()
            }

            appBarUpdateCategory.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        showDeleteDialog()
                        true
                    }
                    else -> false
                }
            }

            tvIconBody.setOnClickListener {
                navController.navigate(R.id.action_updateCategoryFragment_to_categoryIconListFragment)
            }

            btnUpdateCategoryComplete.setOnClickListener {
                if (etNameBody.text.isEmpty()) {
                    Toast.makeText(requireContext(), "이름을 반드시 입력해야 합니다", Toast.LENGTH_SHORT).show()
                } else {
                    this@UpdateCategoryFragment.viewModel.updateCategory()
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

    private fun showDeleteDialog() {
        val dialog =
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.category_delete_dialog_title))
                .setMessage(getString(R.string.category_delete_dialog_message))
                .setPositiveButton(getString(R.string.confirm)) { _, _ -> deleteCategory() }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }

        dialog.show()
    }

    private fun deleteCategory() {
        viewModel.deleteCategory { callback ->
            if (callback) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.category_delete_complete),
                    Toast.LENGTH_SHORT
                ).show()
                navController.popBackStack()
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.cannot_delete_category_due_to_existing_data),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
