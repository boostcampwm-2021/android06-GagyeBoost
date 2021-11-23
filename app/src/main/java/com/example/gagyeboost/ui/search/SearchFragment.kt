package com.example.gagyeboost.ui.search

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentSearchBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModel()

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
                val dialog = SearchCategoryDialog()
                dialog.show(childFragmentManager, dialog.tag)
                childFragmentManager.executePendingTransactions()
            }

            btnSearch.setOnClickListener {
                // TODO 검색 - 검색결과로 이동
            }
        }
    }

    private fun initObserver() {

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
