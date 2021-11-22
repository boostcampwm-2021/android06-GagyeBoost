package com.example.gagyeboost.ui.home.categoryControl

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentUpdateCategoryBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class UpdateCategoryFragment :
    BaseFragment<FragmentUpdateCategoryBinding>(R.layout.fragment_update_category) {
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
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
                                    "데이터가 존재해서 삭제할 수 없습니다.",
                                    Toast.LENGTH_SHORT
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
                Toast.makeText(requireContext(), "이름을 반드시 입력해야 합니다", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.updateCategory()
                navController.popBackStack()
            }
        }
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
                        Toast.LENGTH_SHORT
                    ).show()
                    navController.popBackStack()
                }.setNegativeButton(getString(R.string.cancel)) { _, _ -> }

        dialog.show()
    }

}
