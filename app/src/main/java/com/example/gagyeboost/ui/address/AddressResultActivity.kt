package com.example.gagyeboost.ui.address

import android.os.Bundle
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.ActivityAddressResultBinding
import com.example.gagyeboost.ui.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddressResultActivity :
    BaseActivity<ActivityAddressResultBinding>(R.layout.activity_address_result) {

    private val viewModel by viewModel<AddressResultViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.viewModel = viewModel
    }
}
