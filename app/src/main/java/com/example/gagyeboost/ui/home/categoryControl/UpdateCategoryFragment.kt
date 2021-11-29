package com.example.gagyeboost.ui.home.categoryControl

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
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
        init()
    }

    private fun init() {
        inputMethodManager =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.viewModel = viewModel
        with(binding.appBarUpdateCategory) {
            setNavigationOnClickListener {
                viewModel.resetSelectedCategory()
                navController.popBackStack()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        viewModel.deleteCategory { result ->
                            if (result) {
                                showDeleteDialog()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    getString(R.string.cant_delete_category),
                                    LENGTH_SHORT
                                ).show()
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
        }

        binding.tvIconBody.setOnClickListener {
            navController.navigate(R.id.action_updateCategoryFragment_to_categoryIconListFragment)
        }

        binding.btnUpdateCategoryComplete.setOnClickListener {
            if (binding.etNameBody.text.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.must_enter_category_name),
                    LENGTH_SHORT
                ).show()
            } else {
                viewModel.updateCategory()
                navController.popBackStack()
            }
        }

        binding.root.setOnClickListener {
            inputMethodManager.hideSoftInputFromWindow(binding.etNameBody.windowToken, 0)
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
                .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.category_delete_complete),
                        LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }.setNegativeButton(getString(R.string.cancel)) { _, _ -> }

        dialog.show()
    }

}
