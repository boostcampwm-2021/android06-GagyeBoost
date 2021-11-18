package com.example.gagyeboost.ui.home.selectPosition

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.GPSUtils
import com.example.gagyeboost.common.INTENT_EXTRA_PLACE_DETAIL
import com.example.gagyeboost.databinding.FragmentSelectPositionBinding
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.address.AddressResultActivity
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import com.google.android.gms.maps.CameraUpdateFactory.newLatLng
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SelectPositionFragment :
    BaseFragment<FragmentSelectPositionBinding>(R.layout.fragment_select_position),
    OnMapReadyCallback {

    private val viewModel by sharedViewModel<AddViewModel>()
    private lateinit var navController: NavController
    private lateinit var googleMap: GoogleMap
    private val gpsUtils: GPSUtils by lazy { GPSUtils(requireContext()) }
    private val moveCameraToPlace: (PlaceDetail) -> Unit = {
        val latLng = LatLng(it.lat.toDouble(), it.lng.toDouble())

        googleMap.let { map ->
            map.clear()
            map.addMarker(
                MarkerOptions().position(latLng).title("${it.roadAddressName} ${it.placeName}")
            )
            map.animateCamera(newLatLng(latLng))
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
                val place =
                    result.data?.getSerializableExtra(INTENT_EXTRA_PLACE_DETAIL) as PlaceDetail

                viewModel.selectedLocation = place
                moveCameraToPlace(place)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        binding.viewModel = viewModel

        init()
        initMap()
        requestLocation.launch(permissions)
    }

    private fun init() {
        binding.btnComplete.setOnClickListener {
            navController.popBackStack(R.id.homeFragment, false)
            viewModel.addAccountBookData()
        }

        binding.appBarSelectPosition.setNavigationOnClickListener {
            viewModel.resetSelectedCategory()
            findNavController().popBackStack()
        }

        binding.btnSearch.setOnClickListener {
            goToAddressResultActivity.launch(
                Intent(
                    requireContext(),
                    AddressResultActivity::class.java
                )
            )
        }

        binding.btnGps.setOnClickListener {
            moveCameraToUser()
        }

        viewModel.searchAddress.value = ""
        viewModel.selectedLocation = null
    }

    private fun initMap() {
        binding.map.getMapAsync(this)
        binding.map.onCreate(null)
    }

    private fun moveCameraToUser() {
        val userLocation = gpsUtils.getUserLocation()
        val latLng = LatLng(userLocation.latitude, userLocation.longitude)

        viewModel.userLocation = userLocation

        googleMap.animateCamera(newLatLngZoom(latLng, 15f))
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        val userLocation = gpsUtils.getUserLocation()
        val latLng = LatLng(userLocation.latitude, userLocation.longitude)

        googleMap.moveCamera(newLatLngZoom(latLng, 15f))
    }

}
