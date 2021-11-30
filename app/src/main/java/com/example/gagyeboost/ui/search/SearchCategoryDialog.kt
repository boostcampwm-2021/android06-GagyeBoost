package com.example.gagyeboost.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.DialogSearchCategoryBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class SearchCategoryDialog(private val viewModel: SearchViewModel) : BottomSheetDialogFragment() {

    private var _binding: DialogSearchCategoryBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: SearchCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_search_category,
            null,
            false
        )

        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        binding.toggleGroupMoneyType.check(R.id.btn_expense)

        adapter = SearchCategoryAdapter(viewModel)
        adapter.setHasStableIds(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvCategoryList.adapter = adapter

        this.dialog?.setOnCancelListener {
            viewModel.changeCategoryBackground()
        } ?: Timber.e("setOnCancelListener dialog is null")

        setClickListeners()
        setObservers()
    }

    private fun setClickListeners() {
        with(binding) {
            tvSelectAll.setOnClickListener {
                adapter.setCategoryList(
                    true,
                    toggleGroupMoneyType.checkedButtonId == R.id.btn_expense
                )
            }

            tvSelectClear.setOnClickListener {
                adapter.setCategoryList(
                    false,
                    toggleGroupMoneyType.checkedButtonId == R.id.btn_expense
                )
            }

            toggleGroupMoneyType.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (!isChecked) return@addOnButtonCheckedListener

                if (checkedId == R.id.btn_expense) {
                    adapter.submitList(viewModel!!.getCategoryList(EXPENSE))
                } else {
                    adapter.submitList(viewModel!!.getCategoryList(INCOME))
                }
            }
        }
    }

    private fun setObservers() {
        viewModel.categoryExpenseList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
