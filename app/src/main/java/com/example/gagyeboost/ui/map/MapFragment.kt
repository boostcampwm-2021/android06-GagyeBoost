package com.example.gagyeboost.ui.map

import android.os.Bundle
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.DialogFilterMoneyTypeBinding
import com.example.gagyeboost.databinding.FragmentMapBinding
import com.example.gagyeboost.model.data.MyItem
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.map.filter.FilterMoneyDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.maps.android.clustering.ClusterManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.*

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val viewModel: MapViewModel by sharedViewModel()
    private lateinit var clusterManager: ClusterManager<MyItem>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initObserver()
        setDialog()
    }

    private fun initObserver() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            viewModel.loadFilterData()
        }
    }

    private fun initView() {
        binding.viewModel = viewModel
        viewModel.setInitData()
        binding.mvMap.getMapAsync(this)
        binding.mvMap.onCreate(null)
    }

    private fun setDialog() {
        binding.btnMoney.setOnClickListener {
            val dialog = FilterMoneyDialog()
            dialog.show(childFragmentManager, dialog.tag)
        }
        binding.btnMoneyType.setOnClickListener {
            showMoneyTypeDialog()
        }

        binding.btnPeriod.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showMoneyTypeDialog() {
        val moneyTypeBinding = DialogFilterMoneyTypeBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(moneyTypeBinding.root)

        dialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        moneyTypeBinding.lifecycleOwner = viewLifecycleOwner
        moneyTypeBinding.viewModel = viewModel
        dialog.show()

        moneyTypeBinding.btnIncome.setOnClickListener {
            viewModel.byteMoneyType.value = INCOME
            viewModel.loadFilterData()
            dialog.dismiss()
        }
        moneyTypeBinding.btnExpense.setOnClickListener {
            viewModel.byteMoneyType.value = EXPENSE
            viewModel.loadFilterData()
            dialog.dismiss()
        }
    }

    private fun showDateRangePicker() {
        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setTitleText("Select Date").build()

        dateRangePicker.show(parentFragmentManager, "date_range_picker")

        dateRangePicker.addOnPositiveButtonClickListener { date ->
            viewModel.setPeriod(Date(date.first), Date(date.second))
        }
    }

    override fun onStart() {
        super.onStart()
        binding.mvMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mvMap.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mvMap.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val seoul = LatLng(37.5642135, 127.0016985)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))
//        googleMap.setOnInfoWindowClickListener {
//            viewModel.setSelectedDetail(
//                it.position.latitude.toFloat(),
//                it.position.longitude.toFloat()
//            )
//            val bottomSheet =
//                MapDetailFragment(it.title ?: "", viewModel.selectedDetailList, viewModel)
//            bottomSheet.show(childFragmentManager, bottomSheet.tag)
//        }

        setUpClusterer()
        addItems()

    }

    private fun setUpClusterer() {
        clusterManager = ClusterManager(context, googleMap)
        googleMap.setOnCameraIdleListener(clusterManager)
//        googleMap.setOnMarkerClickListener(clusterManager)
    }

    private fun addItems() {
        viewModel.dataMap.observe(viewLifecycleOwner) {
            googleMap.clear()
            val markerMap = viewModel.hashMapToMarkerMap(it)
            markerMap.forEach { (latLng, addrMoney) ->
                val offsetItem =
                    MyItem(
                        latLng.first.toDouble(),
                        latLng.second.toDouble(),
                        addrMoney.first,
                        addrMoney.second
                    )
                clusterManager.addItem(offsetItem)
            }
        }
    }
}