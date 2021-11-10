package com.example.gagyeboost.ui.home.detail

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Toast
import com.example.gagyeboost.common.DATE_DETAIL_ITEM_ID_KEY
import com.example.gagyeboost.databinding.ActivityRecordDetailBinding
import com.example.gagyeboost.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RecordDetailActivity :
    BaseActivity<ActivityRecordDetailBinding>(com.example.gagyeboost.R.layout.activity_record_detail) {
    private var accountBookId = -1
    private val viewModel: RecordDetailViewModel by viewModel { parametersOf(accountBookId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        setListeners()
    }

    private fun initView() {
        accountBookId = intent.getIntExtra(DATE_DETAIL_ITEM_ID_KEY, 0)
        binding.viewModel = viewModel
    }

    private fun setListeners() {
        with(binding.appBarUpdateRecord) {
            setNavigationOnClickListener {
                onBackPressed()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    com.example.gagyeboost.R.id.delete -> {
                        deleteAccountBookData()
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
                    getString(com.example.gagyeboost.R.string.must_enter_category_name),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                viewModel.updateAccountBookData()
            }
        }

        setDialogs()
    }

    private fun deleteAccountBookData() {
        viewModel.deleteAccountBookData(accountBookId)
        Toast.makeText(
            this, getString(com.example.gagyeboost.R.string.record_delete_success),
            Toast.LENGTH_SHORT
        ).show()
        finish()
    }

    private fun setDialogs() {
        binding.tvDateBody.setOnClickListener {
            showDatePicker()
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
}
