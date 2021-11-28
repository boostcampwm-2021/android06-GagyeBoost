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
import com.example.gagyeboost.model.data.MyItem
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
import timber.log.Timber

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
            if (isValidPosition(this)) this else
                gpsUtils.getUserLatLng()
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
        setObserver()
        viewModel.resetLocation()
    }

    private fun initView() {
        accountBookId = intent.getIntExtra(DATE_DETAIL_ITEM_ID_KEY, 0)
        binding.viewModel = viewModel
        viewModel.setAccountBookData {
            binding.tvAddressBody.text = viewModel.accountBookData.value?.address
            val latitude = viewModel.accountBookData.value?.latitude ?: DEFAULT_LAT
            val longitude = viewModel.accountBookData.value?.longitude ?: DEFAULT_LNG
            val address = viewModel.accountBookData.value?.address
            //val latLng = LatLng(latitude, longitude)
            val placeDetail = PlaceDetail(
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                roadAddressName = address ?: "",
                lat = latitude.toString(),
                lng = longitude.toString()
            )
            viewModel.setPlaceList(listOf(placeDetail))
            moveCameraToPlace(placeDetail)
        }

        categoryAdapter =
            CategoryAdapter({ category -> categoryOnClickListener(category) }, { true }, viewModel)
    }

    private fun setListeners() {
        with(binding.appBarUpdateRecord) {
            setNavigationOnClickListener {
                onBackPressed()
            }

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
            if (binding.tvCategoryBody.text.isEmpty()) {
                Toast.makeText(
                    this,
                    getString(R.string.must_enter_category_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.updateAccountBookData()
                Toast.makeText(this, getString(R.string.update_has_been_completed), LENGTH_SHORT).show()
                finish()
            }
        }

        binding.etAddress.setOnClickListener {
            goToAddressResultActivity.launch(Intent(this, AddressResultActivity::class.java))
        }

        binding.constraintDetail.setOnClickListener {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
        }

        setDialogs()
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
        Toast.makeText(
            this, getString(R.string.record_delete_success),
            Toast.LENGTH_SHORT
        ).show()
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
        val date = viewModel.date.value ?: "2021.07.19"
        val year = date.split(".")[0].toInt()
        val month = date.split(".")[1].toInt() - 1
        val day = date.split(".")[2].toInt()

        DatePickerDialog(
            this, { _, y, m, d -> viewModel.setDate(y, m + 1, d) },
            year, month, day
        ).show()
    }

    private fun showCategoryList() {
        viewModel.loadCategoryList()
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

        viewModel.dateDetailItemMoney.observe(this) {
            viewModel.dateDetailItem.value?.let { item ->
                item.money = it
            }
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
            viewModel.setSelectedPlace(MyItem(DEFAULT_LAT, DEFAULT_LNG, "", ""))
        }

        viewModel.selectedLocationList.observe(this, { placeList ->
            with(googleMap) {
                val icon = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.ic_default_marker,
                    null
                )?.let {
                    BitmapUtils.createBitmapFromDrawable(it)
                }

                clear()
                placeList.forEachIndexed { idx, placeDetail ->
                    addMarker(
                        MarkerOptions().icon(icon?.let { BitmapDescriptorFactory.fromBitmap(it) })
                            .position(
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

        viewModel.selectedLocation.observe(this, { location ->
            binding.etAddress.text = location.title
        })
    }

    private fun addMarkerToPlace(latLng: LatLng) {
        googleMap.clear()

        if (latLng.latitude > 0 && latLng.longitude > 0) {
            val markerOptions = MarkerOptions().apply { position(latLng) }

            ResourcesCompat.getDrawable(
                resources,
                R.drawable.ic_default_marker,
                null
            )?.let {
                val bitmap = BitmapUtils.createBitmapFromDrawable(it)
                markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            }

            googleMap.addMarker(markerOptions)
            googleMap.moveCamera(newLatLngZoom(latLng, 15f))
        } else {
            googleMap.moveCamera(newLatLngZoom(gpsUtils.getUserLatLng(), 15f))
        }
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
