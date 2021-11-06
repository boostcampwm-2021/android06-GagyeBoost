package com.example.gagyeboost.ui.home.selectPosition

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentSelectPositionBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import com.example.gagyeboost.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectPositionFragment :
    BaseFragment<FragmentSelectPositionBinding>(R.layout.fragment_select_position) {
    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init() {
        binding.btnComplete.setOnClickListener {
            navController.popBackStack(R.id.homeFragment, false)
            viewModel.addAccountBookData()
        }
        binding.btnSelectPositionBack.setOnClickListener {
            viewModel.selectedCategoryReset()
            navController.popBackStack()
        }
    }
}
