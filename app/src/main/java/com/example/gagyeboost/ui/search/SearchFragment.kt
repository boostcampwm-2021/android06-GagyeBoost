package com.example.gagyeboost.ui.search

import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.FragmentSearchBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

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
            toggleGroupMoneyType.check(btnExpense.id)
            etKeywordBody.hint = resources.getString(R.string.plz_enter_keyword)
            etKeywordBody.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                etKeywordBody.hint =
                    if (hasFocus) "" else resources.getString(R.string.plz_enter_keyword)
            }
        }
    }

    private fun initListener() {
        with(binding) {
            toggleGroupMoneyType.addOnButtonCheckedListener { _, checkedId, isChecked ->
                if (!isChecked) return@addOnButtonCheckedListener
                this@SearchFragment.viewModel.setSelectedType(if (checkedId == btnExpense.id) EXPENSE else INCOME)
            }
            btnReset.setOnClickListener { this@SearchFragment.viewModel.resetData() }
            btnDateStartBody.setOnClickListener {
                // TODO DatePicker 처음 날짜 지정
            }
            btnDateEndBody.setOnClickListener {
                // TODO DatePicker 마지막 날짜 지정
            }
            btnSelectCategory.setOnClickListener {
                // TODO 카테고리 선택 dialog
            }
            btnMoneyStartBody.setOnClickListener {
                // TODO 최소금액 지정
            }
            btnMoneyEndBody.setOnClickListener {
                // TODO 최대금액 지정
            }
            btnSearch.setOnClickListener {
                // TODO 검색 - 검색결과로 이동
            }
        }
    }

    private fun initObserver() {
        viewModel.selectedType.observe(viewLifecycleOwner, {
            binding.toggleGroupMoneyType.check(if (it == INCOME) binding.btnIncome.id else binding.btnExpense.id)
        })
    }
}
