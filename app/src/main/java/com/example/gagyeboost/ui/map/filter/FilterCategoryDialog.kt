package com.example.gagyeboost.ui.map.filter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.DialogFilterCategoryBinding
import com.example.gagyeboost.ui.map.MapViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class FilterCategoryDialog : BottomSheetDialogFragment() {

    private var _binding: DialogFilterCategoryBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MapViewModel by sharedViewModel()
    private lateinit var adapter: FilterCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        _binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_filter_category,
            null,
            false
        )
        adapter = FilterCategoryAdapter(viewModel)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.rvFilterCategory.adapter = adapter
        adapter.submitList(viewModel.getCategoryList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
