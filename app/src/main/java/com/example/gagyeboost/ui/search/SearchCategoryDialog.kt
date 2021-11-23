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
import com.example.gagyeboost.ui.map.filter.FilterCategoryAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class SearchCategoryDialog : BottomSheetDialogFragment() {

    private var _binding: DialogSearchCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var adapter: FilterCategoryAdapter

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

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FilterCategoryAdapter(viewModel, viewModel.expenseCategoryID ?: listOf())
        binding.rvCategoryList.adapter = adapter
        adapter.submitList(viewModel.getCategoryList(EXPENSE))

        this.dialog?.setOnCancelListener {
            viewModel.loadFilterData()
            viewModel.changeCategoryBackground()
        } ?: Timber.e("setOnCancelListener dialog is null")

        setClickListeners()
    }

    private fun setClickListeners() {
        with(binding) {
            tvSelectAll.setOnClickListener {
                adapter.setCategoryList(true)
            }

            tvSelectClear.setOnClickListener {
                adapter.setCategoryList(false)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
