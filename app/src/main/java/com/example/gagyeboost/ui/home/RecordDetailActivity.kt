package com.example.gagyeboost.ui.home

import android.os.Bundle
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.example.gagyeboost.R
import com.example.gagyeboost.common.DATE_DETAIL_ITEM_ID_KEY
import com.example.gagyeboost.databinding.ActivityRecordDetailBinding
import com.example.gagyeboost.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class RecordDetailActivity :
    BaseActivity<ActivityRecordDetailBinding>(R.layout.activity_record_detail) {
    private var accountBookId = -1
    private val viewModel: RecordDetailViewModel by viewModel { parametersOf(accountBookId) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        accountBookId = intent.getIntExtra(DATE_DETAIL_ITEM_ID_KEY, 0)
        binding.viewModel = viewModel

        with(binding.appBarUpdateRecord) {
            setNavigationOnClickListener {
                onBackPressed()
            }

            setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.delete -> {
                        viewModel.deleteAccountBookData(accountBookId)
                        Toast.makeText(
                            this@RecordDetailActivity,
                            context.getString(R.string.record_delete_success),
                            LENGTH_SHORT
                        ).show()
                        finish()
                        true
                    }
                    else -> false
                }
            }
        }
    }
}
