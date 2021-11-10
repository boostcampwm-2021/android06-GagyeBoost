package com.example.gagyeboost.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentMapBinding
import com.example.gagyeboost.ui.base.BaseFragment
import com.google.android.gms.maps.GoogleMap
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map) {

    private lateinit var googleMap: GoogleMap
    private val viewModel: MapViewModel by sharedViewModel()
    private lateinit var dialog: FilterDialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        viewModel.categoryList.observe(viewLifecycleOwner) {
            viewModel.loadFilterData()
        }

        Log.e("map fragment", "onViewCreated")

        binding.btnTest.setOnClickListener {
            dialog.show()
        }
    }

    private fun initView() {
        binding.viewModel = viewModel
        viewModel.setInitData()
        dialog = FilterDialog(binding.root.context, viewModel)

        dialog.isShowing
    }
}
