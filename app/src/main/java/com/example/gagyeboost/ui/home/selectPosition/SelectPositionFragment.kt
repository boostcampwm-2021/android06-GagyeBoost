package com.example.gagyeboost.ui.home.selectPosition

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
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

        binding.etAddress.setOnEditorActionListener { view, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (viewModel.searchAddress.value!!.isNotEmpty()) {
                    binding.pbLoading.isVisible = true

                    viewModel.getPlaceListData(view.text.toString()).observe(viewLifecycleOwner) {
                        it.getOrNull()?.let { list ->
                            val bottom = AddressResultFragment(list, viewModel)
                            bottom.show(childFragmentManager, bottom.tag)
                        } ?: run {
                            Toast.makeText(requireContext(), "결과가 없습니다.", Toast.LENGTH_LONG).show()
                        }

                        binding.pbLoading.isVisible = false
                    }
                }
            }

            true
        }

        binding.btnGps.setOnClickListener {
            moveCameraToUser()
        }

        viewModel.searchAddress.value = ""

        viewModel.selectedAddress.observe(viewLifecycleOwner) {
            val latLng = LatLng(it.geometry.location.lat, it.geometry.location.lng)
            googleMap.clear()
            googleMap.addMarker(
                MarkerOptions().position(latLng).title(it.formattedAddress)
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
