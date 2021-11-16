package com.example.gagyeboost.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.gagyeboost.R
import com.example.gagyeboost.common.EXPENSE
import com.example.gagyeboost.common.INCOME
import com.example.gagyeboost.databinding.DialogFilterMoneyTypeBinding
import com.example.gagyeboost.databinding.FragmentMapBinding
import com.example.gagyeboost.model.data.MyItem
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.map.filter.FilterCategoryDialog
import com.example.gagyeboost.ui.map.filter.FilterMoneyDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.collections.MarkerManager
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import timber.log.Timber
import java.util.*

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val viewModel: MapViewModel by sharedViewModel()
    private lateinit var clusterManager: ClusterManager<MyItem?>
    private lateinit var markerManager: MarkerManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setDialog()
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
        binding.btnCategory.setOnClickListener {
            val dialog = FilterCategoryDialog()
            dialog.show(childFragmentManager, dialog.tag)
            childFragmentManager.executePendingTransactions()
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
            viewModel.setCategoryIDList(INCOME)
            viewModel.loadFilterData()
            dialog.dismiss()
        }
        moneyTypeBinding.btnExpense.setOnClickListener {
            viewModel.byteMoneyType.value = EXPENSE
            viewModel.setCategoryIDList(EXPENSE)
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
            viewModel.loadFilterData()
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
        setUpMap()
        addItems()
        clickListener()
    }

    private fun setUpMap() {
        markerManager = MarkerManager(googleMap)
        clusterManager = ClusterManager(context, googleMap, markerManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        // TODO 내위치 설정
        val myLocation = LatLng(37.5642135, 127.0016985)
        // TODO 설정된 위치로 이동
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15f))
        clusterManager.renderer = MyClusterRenderer(context, googleMap, clusterManager)
    }

    private fun addItems() {
        viewModel.dataMap.observe(viewLifecycleOwner) {
            googleMap.clear()
            clusterManager.clearItems()
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

    private fun clickListener() {
        clusterManager.setOnClusterItemClickListener { item: MyItem? ->
            // 마커 클릭
            Timber.e("setOnClusterItemClickListener click")
            false
        }
        clusterManager.setOnClusterClickListener { item: Cluster<MyItem?> ->
            //클러스터링 된 item 클릭
            Timber.e("setOnClusterClickListener click")
            false
        }

        clusterManager.markerCollection.setOnInfoWindowClickListener { marker ->
            viewModel.setSelectedDetail(
                marker.position.latitude.toFloat(),
                marker.position.longitude.toFloat()
            )
            val bottomSheet =
                MapDetailFragment(marker.title ?: "주소 없음", viewModel.selectedDetailList, viewModel) {
                    viewModel.loadFilterData()
                }
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
            Timber.e("setOnInfoWindowClickListener click")
        }
    }
}
