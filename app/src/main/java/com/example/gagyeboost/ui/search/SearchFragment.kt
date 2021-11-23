package com.example.gagyeboost.ui.search

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentSearchBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.detail.DateDetailAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModel()
    private lateinit var adapter: DateDetailAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }

    private fun initView() {
        with(binding) {
            viewModel = this@SearchFragment.viewModel

            etKeywordBody.requestFocus()

            adapter = DateDetailAdapter { true }
            rvSearchResult.adapter = adapter
        }
    }

    private fun initListener() {
        with(binding) {
            appBarSearch.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.refresh -> {
                        this@SearchFragment.viewModel.resetData()
                        true
                    }
                    else -> false
                }
            }

            btnDateStartBody.setOnClickListener {
                showDatePicker(true)
            }

            btnDateEndBody.setOnClickListener {
                showDatePicker(false)
            }

            btnSelectCategory.setOnClickListener {
                val dialog = SearchCategoryDialog(viewModel!!)
                dialog.show(childFragmentManager, dialog.tag)
                childFragmentManager.executePendingTransactions()
            }

            btnSearch.setOnClickListener {
                viewModel?.loadFilterData()

                // 키보드 내리기
                val inputMethodManager =
                    requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(binding.etKeywordBody.windowToken, 0)
            }
        }
    }

    private fun initObserver() {
        viewModel.filteredResult.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    private fun showDatePicker(isStart: Boolean) {
        val listener = { y: Int, m: Int, d: Int ->
            if (isStart) viewModel.setStartDate(y, m, d)
            else viewModel.setEndDate(y, m, d)
        }
        val year =
            if (isStart) viewModel.startYear.value ?: 1970 else viewModel.endYear.value ?: 2500
        val month = if (isStart) viewModel.startMonth.value ?: 1 else viewModel.endMonth.value ?: 12
        val day = if (isStart) viewModel.startDay.value ?: 1 else viewModel.endDay.value ?: 1

        DatePickerDialog(
            requireContext(), { _, y, m, d ->
                listener(y, m + 1, d)
            },
            year, month - 1, day
        ).show()
    }
}
