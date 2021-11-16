package com.example.gagyeboost.ui.home.selectPosition

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.gagyeboost.common.GPSUtils
import com.example.gagyeboost.databinding.DialogBottomAddressResultBinding
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.home.AddViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AddressResultFragment(
    private val viewModel: AddViewModel,
    private val moveCameraToPlace: (PlaceDetail) -> Unit,
    private val keyword: String,
    private val dismissCallback: () -> Unit
) :
    BottomSheetDialogFragment() {

    private var _binding: DialogBottomAddressResultBinding? = null
    private val binding get() = _binding!!
    private val adapter by lazy {
        AddressAdapter(viewModel) {
            dismissCallback()
            dismiss()
            moveCameraToPlace(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding =
            DialogBottomAddressResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvAddress.adapter = adapter

        lifecycleScope.launch {
            viewModel.fetchPlaceListData(keyword, GPSUtils(requireContext()).getUserLatLng()) {
                Toast.makeText(requireContext(), "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
                dismissCallback()
                dismiss()
            }.collectLatest {
                (binding.rvAddress.adapter as AddressAdapter).submitData(it)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        dismissCallback()
        super.onDestroyView()
    }
}
