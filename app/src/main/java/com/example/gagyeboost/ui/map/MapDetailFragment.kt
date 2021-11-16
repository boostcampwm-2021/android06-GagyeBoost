package com.example.gagyeboost.ui.map

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LiveData
import com.example.gagyeboost.common.DATE_DETAIL_ITEM_ID_KEY
import com.example.gagyeboost.databinding.DialogMapBottomDetailBinding
import com.example.gagyeboost.model.data.DateDetailItem
import com.example.gagyeboost.ui.home.detail.RecordDetailActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MapDetailFragment(
    private val address: String,
    private val liveDetailList: LiveData<List<DateDetailItem>>,
    private val viewModel: MapViewModel
) : BottomSheetDialogFragment() {

    private var _binding: DialogMapBottomDetailBinding? = null
    private val binding get() = _binding!!

    private val adapter: DetailAdapter by lazy {
        DetailAdapter {
            // TODO("MapDetailFragment 돌아왔을 때 수정사항 반영")
            startActivity(Intent(requireContext(), RecordDetailActivity::class.java).apply {
                putExtra(DATE_DETAIL_ITEM_ID_KEY, id)
            })
            true
        }.apply {
            submitList(liveDetailList.value)
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
        initObserver()
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    private fun initObserver() {
        liveDetailList.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }
}
