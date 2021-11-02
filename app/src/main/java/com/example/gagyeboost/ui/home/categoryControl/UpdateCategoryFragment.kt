package com.example.gagyeboost.ui.home.categoryControl

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentUpdateCategoryBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class UpdateCategoryFragment :
    BaseFragment<FragmentUpdateCategoryBinding>(R.layout.fragment_update_category) {
    private val viewModel: MainViewModel by viewModel()
    private lateinit var navController: NavController

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        init()
    }

    private fun init(){
        binding.btnUpdateCategoryBack.setOnClickListener{
            navController.popBackStack()
        }
    }

}