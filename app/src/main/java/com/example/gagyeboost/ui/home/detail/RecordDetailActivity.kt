package com.example.gagyeboost.ui.home.detail

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.res.ResourcesCompat
import com.example.gagyeboost.R
import com.example.gagyeboost.common.*
import com.example.gagyeboost.databinding.ActivityRecordDetailBinding
import com.example.gagyeboost.databinding.BottomSheetCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.address.AddressResultActivity
import com.example.gagyeboost.ui.base.BaseActivity
import com.example.gagyeboost.ui.home.category.CategoryAdapter
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RecordDetailActivity :
    BaseActivity<ActivityRecordDetailBinding>(R.layout.activity_record_detail),
    OnMapReadyCallback {

    private var accountBookId = -1
    private val viewModel: RecordDetailViewModel by viewModel { parametersOf(accountBookId) }
    private val gpsUtils by lazy { GPSUtils(this) }
    private lateinit var googleMap: GoogleMap
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val moveCameraToPlace: (PlaceDetail) -> Unit = {
        val latLng = LatLng(it.lat.toDouble(), it.lng.toDouble())
        googleMap.moveCamera(newLatLngZoom(latLng.run {
            if (isValidPosition(this)) this else gpsUtils.getUserLatLng()
        }, 15f))

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMap()
        initView()
        setListeners()
        setDialogs()
        setObserver()
    }

    private fun initView() {
        accountBookId = intent.getIntExtra(DATE_DETAIL_ITEM_ID_KEY, 0)
        binding.viewModel = viewModel

        categoryAdapter =
            CategoryAdapter({ category -> categoryOnClickListener(category) }, { true }, viewModel)
    }

    private fun setListeners() {
        with(binding.appBarUpdateRecord) {
            setNavigationOnClickListener { onBackPressed() }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        showDeleteDialog()
                        true
                    }
                    else -> false
                }
            }
        }

        binding.btnUpdate.setOnClickListener {
            viewModel.updateAccountBookData()
            Toast.makeText(this, getString(R.string.update_has_been_completed), LENGTH_SHORT).show()
            finish()
        }

        binding.etAddress.setOnClickListener {
            goToAddressResultActivity.launch(Intent(this, AddressResultActivity::class.java))
        }

        binding.constraintDetail.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }
    }

    private fun initMap() {
        binding.map.getMapAsync(this)
        binding.map.onCreate(null)
    }

    private fun showDeleteDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle(getString(R.string.delete_record))
            .setMessage(getString(R.string.record_delete_dialog_message))
            .setPositiveButton(getString(R.string.confirm)) { _, _ -> deleteAccountBookData() }
            .setNegativeButton(R.string.cancel) { _, _ -> }

        builder.show()
    }

    private fun deleteAccountBookData() {
        viewModel.deleteAccountBookData(accountBookId)
        Toast.makeText(this, getString(R.string.record_delete_success), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun setDialogs() {
        binding.tvDateBody.setOnClickListener {
            showDatePicker()
        }

        binding.tvCategoryBody.setOnClickListener {
            showCategoryList()
        }
    }

    private fun showDatePicker() {
        val date = (viewModel.date.value ?: "2021.07.19").split(".").map { it.toInt() }

        DatePickerDialog(
            this, { _, y, m, d -> viewModel.setDate(y, m + 1, d) },
            date[0], date[1] - 1, date[2]
        ).show()
    }

    private fun showCategoryList() {
        val binding = BottomSheetCategoryBinding.inflate(layoutInflater)
        binding.rvCategory.adapter = categoryAdapter
        bottomSheetDialog = BottomSheetDialog(this)

        bottomSheetDialog.setContentView(binding.root)
        bottomSheetDialog.behavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetDialog.show()
    }

    private fun setObserver() {
        viewModel.categoryList.observe(this) {
            categoryAdapter.submitList(it)
        }
    }

    private fun categoryOnClickListener(category: Category): Boolean {
        viewModel.setCategory(category)
        bottomSheetDialog.dismiss()
        return true
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

        googleMap.setOnMarkerClickListener {
            selectLocation(it)
            true
        }

        googleMap.setOnInfoWindowCloseListener {
            viewModel.setSelectedPlace(null)
        }

        viewModel.placeDetail.observe(this) { placeDetail -> moveCameraToPlace(placeDetail) }

        viewModel.searchPlaceList.observe(this, { placeList ->
            with(googleMap) {
                val icon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_default_marker,
                    null
                )?.let { BitmapUtils.createBitmapFromDrawable(it) }

                clear()
                placeList.forEachIndexed { idx, placeDetail ->
                    addMarker(
                        MarkerOptions().icon(icon?.let { BitmapDescriptorFactory.fromBitmap(it) })
                            .position(
                                LatLng(placeDetail.lat.toDouble(), placeDetail.lng.toDouble())
                            )
                            .title(if (placeDetail.placeName.isEmpty()) placeDetail.roadAddressName else placeDetail.placeName)
                    )?.let {
                        if (idx == 0) selectLocation(it)
                    }
                }
            }
        })
    }

    private fun selectLocation(marker: Marker) {
        marker.showInfoWindow()
        viewModel.setSelectedPlace(marker)
    }
}
