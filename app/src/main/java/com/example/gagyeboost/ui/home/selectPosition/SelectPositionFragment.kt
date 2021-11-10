package com.example.gagyeboost.ui.home.selectPosition

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.example.gagyeboost.R
import com.example.gagyeboost.common.GPSUtils
import com.example.gagyeboost.databinding.FragmentSelectPositionBinding
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
    private val permissions = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
    private val requestLocation = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        moveCameraToUser()
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

        binding.etAddress.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val address = viewModel.getAddress(Geocoder(requireContext()))

                if (address.isNotEmpty()) {
                    val bottom = AddressResultFragment(address, viewModel)
                    bottom.show(childFragmentManager, bottom.tag)
                }
            }

            true
        }

        binding.btnGps.setOnClickListener {
            moveCameraToUser()
        }

        viewModel.searchAddress.value = ""

        viewModel.selectedAddress.observe(viewLifecycleOwner) {
            val latLng = LatLng(it.latitude, it.longitude)
            googleMap.addMarker(
                MarkerOptions().position(latLng).title(it.getAddressLine(0))
            )

            googleMap.animateCamera(newLatLng(latLng))
        }
    }

    private fun initMap() {
        binding.map.getMapAsync(this)
        binding.map.onCreate(null)
    }

    private fun moveCameraToUser() {
        val userLocation = gpsUtils.getUserLocation()
        val latLng = LatLng(userLocation.latitude, userLocation.longitude)

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

