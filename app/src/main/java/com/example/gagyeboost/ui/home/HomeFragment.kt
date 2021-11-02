package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentHomeBinding
import com.example.gagyeboost.ui.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding>(R.layout.fragment_home) {

    private val viewModel: HomeViewModel by viewModel()
    private val customCalendarAdapter = CustomCalendarAdapter {
        Toast.makeText(requireContext(), it + "CLICKED", Toast.LENGTH_SHORT).show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        binding.tvMonth.setOnClickListener {
            setDialog()
        }
    }

    private fun setDialog() {

        val dialog = NumberPickerDialog(binding.root.context)
        dialog.window?.setGravity(Gravity.TOP)
        dialog.show()

        dialog.binding.tvAgree.setOnClickListener {
            val year = dialog.binding.npYear.value
            val month = dialog.binding.npMonth.value
            viewModel.setYear(year)
            viewModel.setMonth(month)
            dialog.dismiss()
        }
        dialog.binding.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun initView() {
        binding.viewModel = viewModel
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
