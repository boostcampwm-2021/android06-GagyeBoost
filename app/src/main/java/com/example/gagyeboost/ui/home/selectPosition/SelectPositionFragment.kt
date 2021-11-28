package com.example.gagyeboost.ui.home.selectPosition

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.*
import com.example.gagyeboost.databinding.FragmentSelectPositionBinding
import com.example.gagyeboost.model.data.MyItem
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.address.AddressResultActivity
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import org.koin.androidx.navigation.koinNavGraphViewModel

class SelectPositionFragment :
    BaseFragment<FragmentSelectPositionBinding>(R.layout.fragment_select_position),
    OnMapReadyCallback {

    private val viewModel by koinNavGraphViewModel<AddViewModel>(R.id.addMoneyGraph)
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap
    private val gpsUtils: GPSUtils by lazy { GPSUtils(requireContext()) }
    private val moveCameraToPlace: (PlaceDetail) -> Unit = {
        val latLng = LatLng(it.lat.toDouble(), it.lng.toDouble())

        val markerOptions = MarkerOptions()

        ResourcesCompat.getDrawable(
            resources,
            R.drawable.ic_default_marker,
            null
        )?.let { drawable ->
            val bitmap = BitmapUtils.createBitmapFromDrawable(drawable)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }

        googleMap.let { map ->
            map.clear()
            map.addMarker(
                markerOptions.position(latLng).title("${it.roadAddressName} ${it.placeName}")
            )
            map.animateCamera(newLatLngZoom(latLng, 15f))
        }
    }
    private val permissions = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    private val requestLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        moveCameraToUser()
    }

    private val goToAddressResultActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val placeList =
                    result.data?.getSerializableExtra(INTENT_EXTRA_PLACE_DETAIL) as Array<PlaceDetail>
                viewModel.setPlaceList(placeList.toList())
                moveCameraToPlace(placeList.firstOrNull() ?: return@registerForActivityResult)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.viewModel = viewModel

        init()
        initMap()
        viewModel.resetLocation()
    }

    private fun init() {
        binding.btnComplete.setOnClickListener {
            viewModel.selectedLocation.value?.let {
                if (isValidPosition(it.position)) {
                    viewModel.addAccountBookData {
                        navController.popBackStack(R.id.homeFragment, false)
                    }
                } else {
                    showNoPlaceDialog()
                }
            } ?: run {
                showNoPlaceDialog()
            }
        }

        binding.appBarSelectPosition.setNavigationOnClickListener {
            viewModel.resetSelectedCategory()
            findNavController().popBackStack()
        }

        binding.btnSearch.setOnClickListener {
            goToAddressResultActivity.launch(
                Intent(requireContext(), AddressResultActivity::class.java)
            )
        }

        binding.btnGps.setOnClickListener {
            moveCameraToUser()
        }
    }

    private fun initMap() {
        binding.map.getMapAsync(this)
        binding.map.onCreate(null)
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

        val marker = MarkerOptions()

        ResourcesCompat.getDrawable(resources, R.drawable.ic_user_marker, null)?.let {
            val bitmap = BitmapUtils.createBitmapFromDrawable(it)
            marker.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            marker.position(userLocation)
            googleMap.addMarker(marker)
        }
    }

    private fun showNoPlaceDialog() {
        val builder = AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.select_place))
            .setMessage(getString(R.string.select_place_dialog_message))
            .setPositiveButton(getString(R.string.confirm)) { _, _ ->
                viewModel.addAccountBookData {
                    navController.popBackStack(R.id.homeFragment, false)
                }
            }
            .setNegativeButton(getString(R.string.cancel)) { _, _ -> }

        builder.show()
    }

    override fun onStart() {
        super.onStart()
        binding.map.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.map.onStop()
    }

    @SuppressLint("PotentialBehaviorOverride")
    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        requestLocation.launch(permissions)

        googleMap.setOnMarkerClickListener {
            selectLocation(it)
            true
        }

        googleMap.setOnInfoWindowCloseListener {
            viewModel.setSelectedPlace(MyItem(-1.0, -1.0, "", ""))
        }

        viewModel.selectedLocationList.observe(viewLifecycleOwner, { placeList ->
            with(googleMap) {
                clear()
                placeList.forEachIndexed { idx, placeDetail ->
                    addMarker(
                        MarkerOptions().position(
                            LatLng(
                                placeDetail.lat.toDouble(),
                                placeDetail.lng.toDouble()
                            )
                        ).title("${placeDetail.roadAddressName} ${placeDetail.placeName}")
                    )?.let {
                        if (idx == 0) selectLocation(it)
                    }
                }
            }
        })

        viewModel.selectedLocation.observe(viewLifecycleOwner, { location ->
            binding.btnSearch.text = location.title
        })
    }

    private fun selectLocation(marker: Marker) {
        marker.showInfoWindow()
        viewModel.setSelectedPlace(
            MyItem(
                marker.position.latitude,
                marker.position.longitude,
                marker.title ?: "",
                marker.snippet ?: ""
            )
        )
    }
}
