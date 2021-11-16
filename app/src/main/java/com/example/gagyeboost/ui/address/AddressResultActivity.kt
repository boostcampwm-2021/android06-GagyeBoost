package com.example.gagyeboost.ui.address

import android.os.Bundle
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.gagyeboost.R
import com.example.gagyeboost.common.GPSUtils
import com.example.gagyeboost.databinding.ActivityAddressResultBinding
import com.example.gagyeboost.ui.base.BaseActivity
import com.example.gagyeboost.ui.home.selectPosition.AddressAdapter
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.subjects.BehaviorSubject
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.concurrent.TimeUnit

class AddressResultActivity :
    BaseActivity<ActivityAddressResultBinding>(R.layout.activity_address_result) {

    private val viewModel by viewModel<AddressResultViewModel>()
    private val gpsUtils by lazy { GPSUtils(this) }
    private val adapter by lazy {
        AddressAdapter(viewModel) {

        }
    }

    private lateinit var disposable: Disposable
    private val observable = BehaviorSubject.create<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
        initObserve()
    }

    private fun initView() {
        binding.rvAddress.adapter = adapter
        binding.viewModel = viewModel

        binding.etSearch.doAfterTextChanged {
            observable.onNext(it?.toString())
        }
    }

    private fun initObserve() {
        disposable = observable.debounce(400, TimeUnit.MILLISECONDS).subscribe {
            lifecycleScope.launch {
                viewModel.fetchPlaceListData(it, gpsUtils.getUserLatLng(), setProgressBarVisible)
                    .collectLatest {
                        (binding.rvAddress.adapter as AddressAdapter).submitData(it)
                    }
            }
        }
    }

    private val setProgressBarVisible: (Boolean) -> Unit = { isVisible ->
        binding.pbLoading.isVisible = isVisible
    }

    override fun onDestroy() {
        disposable.dispose()
        super.onDestroy()
    }
}
