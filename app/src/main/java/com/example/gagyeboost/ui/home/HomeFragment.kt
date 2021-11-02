package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.MainViewModel
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val homeViewModel: HomeViewModel by viewModel()
    private val viewModel by sharedViewModel<MainViewModel>()

    private val customCalendarAdapter = CustomCalendarAdapter {
        Toast.makeText(requireContext(), it + "CLICKED", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        initView()

        binding.tvMonth.setOnClickListener {
            val dialog = NumberPickerDialog(binding.root.context)
            dialog.window?.setGravity(Gravity.TOP)
            dialog.show()
        }
        viewModel.getMonthIncome()
        viewModel.getMonthExpense()
        viewModel.setTotalMoney()
        binding.fabAdd.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addFragment)
        }
    }

    private fun initView() {

        with(binding.rvCalendar) {
            adapter = customCalendarAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.HORIZONTAL
                )
            )
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
    }
}
