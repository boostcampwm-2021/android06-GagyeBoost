package com.example.gagyeboost.ui.map

import android.Manifest
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.core.util.Pair
import com.example.gagyeboost.R
import com.example.gagyeboost.common.*
import com.example.gagyeboost.databinding.DialogFilterMoneyTypeBinding
import com.example.gagyeboost.databinding.FragmentMapBinding
import com.example.gagyeboost.model.data.MyItem
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.map.filter.FilterCategoryDialog
import com.example.gagyeboost.ui.map.filter.FilterMoneyDialog
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
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
    private val gpsUtils by lazy { GPSUtils(requireContext()) }
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    private val requestLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        moveCameraToUser()
        if (::googleMap.isInitialized) {
            googleMap.projection.visibleRegion.latLngBounds.run {
                viewModel.resizeBound(
                    southwest.latitude,
                    northeast.latitude,
                    southwest.longitude,
                    northeast.longitude
                )
            }
        }
    }
    private var currPositionMarker: Marker? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setDialog()
    }

    private fun initView() {
        binding.viewModel = viewModel
        binding.mvMap.getMapAsync(this)
        binding.mvMap.onCreate(null)
        binding.btnGps.setOnClickListener {
            moveCameraToUser()
        }
        binding.btnFilterRefresh.setOnClickListener {
            viewModel.setInitData()
        }
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
        val start = dateToLong(viewModel.startYear, viewModel.startMonth, viewModel.startDay)
        val end = dateToLong(viewModel.endYear, viewModel.endMonth, viewModel.endDay)

        val dateRangePicker = MaterialDatePicker.Builder
            .dateRangePicker()
            .setSelection(Pair(start, end))
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
        clickListener()
        initObserver()
        viewModel.setInitData()
        requestLocation.launch(permissions)
    }

    private fun setUpMap() {
        markerManager = MarkerManager(googleMap)
        clusterManager = ClusterManager(context, googleMap, markerManager)
        googleMap.setOnCameraIdleListener(clusterManager)
        clusterManager.renderer =
            MyClusterRenderer(requireContext(), googleMap, clusterManager, viewModel.intMoneyType)

        googleMap.setOnCameraMoveListener {
            googleMap.projection.visibleRegion.latLngBounds.run {
                viewModel.resizeBound(
                    southwest.latitude,
                    northeast.latitude,
                    southwest.longitude,
                    northeast.longitude
                )
            }
        }
    }

    private fun initObserver() {
        viewModel.dataMap.observe(viewLifecycleOwner) {
            val markerMap = viewModel.hashMapToMarkerMap(it)
            addItem(markerMap)
        }
    }

    private fun addItem(markerMap: HashMap<kotlin.Pair<Double, Double>, kotlin.Pair<String, String>>) {
        googleMap.clear()
        clusterManager.clearItems()
        markerMap.forEach { (latLng, addrMoney) ->
            val offsetItem =
                MyItem(
                    latLng.first,
                    latLng.second,
                    addrMoney.first,
                    addrMoney.second
                )
            clusterManager.addItem(offsetItem)
        }
        clusterManager.cluster()
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
                marker.position.latitude,
                marker.position.longitude
            )
            val bottomSheet =
                MapDetailFragment(
                    marker.title ?: "주소 없음",
                    viewModel.selectedDetailList,
                    viewModel
                ) {
                    viewModel.loadFilterData()
                }
            bottomSheet.show(childFragmentManager, bottomSheet.tag)
            Timber.e("setOnInfoWindowClickListener click")
        }
    }

    private fun moveCameraToUser() {
        val userLocation = gpsUtils.getUserLatLng()
        val cameraPosition = CameraPosition.Builder()
            .target(userLocation)
            .zoom(15f)
            .bearing(0f)
            .tilt(0f)
            .build()

        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        currPositionMarker?.let {
            (currPositionMarker as Marker).remove()
        }

        val marker = MarkerOptions()

        ResourcesCompat.getDrawable(resources, R.drawable.ic_user_marker, null)?.let {
            val bitmap = BitmapUtils.createBitmapFromDrawable(it)
            marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            marker.position(userLocation)
            currPositionMarker = googleMap.addMarker(marker)
        }
    }
}
