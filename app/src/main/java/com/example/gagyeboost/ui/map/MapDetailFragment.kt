package com.example.gagyeboost.ui.map

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.gagyeboost.databinding.DialogMapBottomDetailBinding
import com.example.gagyeboost.model.data.DateDetailItem
import com.example.gagyeboost.ui.home.DateDetailAdapter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MapDetailFragment(
    private val address: String,
    private val list: List<DateDetailItem>,
    private val viewModel: TempViewModel
) : BottomSheetDialogFragment() {

    private var _binding: DialogMapBottomDetailBinding? = null
    private val binding get() = _binding!!

    private val adapter: DetailAdapter by lazy {
        DetailAdapter {
            Log.i("MapDetailFragment", "dialog")
            true
        }.apply {
            submitList(list)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMapBottomDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvDetailAddress.text = address
        binding.rvDetailList.adapter = adapter
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}