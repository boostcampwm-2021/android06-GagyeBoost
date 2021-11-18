package com.example.gagyeboost.ui.home.detail

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.gagyeboost.R
import com.example.gagyeboost.common.*
import com.example.gagyeboost.databinding.ActivityRecordDetailBinding
import com.example.gagyeboost.databinding.BottomSheetCategoryBinding
import com.example.gagyeboost.model.data.Category
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.ui.address.AddressResultActivity
import com.example.gagyeboost.ui.base.BaseActivity
import com.example.gagyeboost.ui.home.category.CategoryAdapter
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
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
    private lateinit var googleMap: GoogleMap
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val goToAddressResultActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                val placeDetail =
                    result.data?.getSerializableExtra(INTENT_EXTRA_PLACE_DETAIL) as PlaceDetail

                viewModel.placeDetail = placeDetail
                addMarkerToPlace(LatLng(placeDetail.lat.toDouble(), placeDetail.lng.toDouble()))
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMap()
        initView()
        setListeners()
        setObserver()
    }

    private fun initView() {
        accountBookId = intent.getIntExtra(DATE_DETAIL_ITEM_ID_KEY, 0)
        binding.viewModel = viewModel
        viewModel.setAccountBookData {
            val latitude = viewModel.accountBookData.value?.latitude?.toDouble() ?: DEFAULT_LAT
            val longitude = viewModel.accountBookData.value?.longitude?.toDouble() ?: DEFAULT_LNG

            val latLng = LatLng(latitude, longitude)
            addMarkerToPlace(latLng)
        }

        categoryAdapter =
            CategoryAdapter({ category -> categoryOnClickListener(category) }, { true })
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

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun addMarkerToPlace(latLng: LatLng) {
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().apply { position(latLng) })
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }
}
