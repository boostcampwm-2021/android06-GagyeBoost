package com.example.gagyeboost.ui.home.selectPosition

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
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
import com.example.gagyeboost.common.BitmapUtils
import com.example.gagyeboost.common.GPSUtils
import com.example.gagyeboost.common.INTENT_EXTRA_PLACE_DETAIL
import com.example.gagyeboost.databinding.FragmentSelectPositionBinding
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.address.AddressResultActivity
import com.example.gagyeboost.ui.base.BaseFragment
import com.example.gagyeboost.ui.home.AddViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.CameraUpdateFactory.newLatLng
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
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
    }

    private fun init() {
        binding.btnComplete.setOnClickListener {
            viewModel.selectedLocation?.let {
                viewModel.addAccountBookData()
                navController.popBackStack(R.id.homeFragment, false)
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

        viewModel.searchAddress.value = ""
        viewModel.selectedLocation = null
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
                viewModel.addAccountBookData()
                navController.popBackStack(R.id.homeFragment, false)
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        requestLocation.launch(permissions)
    }

}
