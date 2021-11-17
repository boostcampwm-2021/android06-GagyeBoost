package com.example.gagyeboost.ui.search

import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentSearchBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : BaseFragment<FragmentSearchBinding>(R.layout.fragment_search) {
    private val viewModel: SearchViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {
            viewModel=this@SearchFragment.viewModel
            etKeywordBody.hint = resources.getString(R.string.content)
            etKeywordBody.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
                etKeywordBody.hint = if (hasFocus) "" else resources.getString(R.string.content)
            }
        }
    }

    private fun initListener() {

    }

}
