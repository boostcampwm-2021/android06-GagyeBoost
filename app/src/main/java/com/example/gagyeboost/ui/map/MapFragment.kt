package com.example.gagyeboost.ui.map

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gagyeboost.R
import com.example.gagyeboost.databinding.FragmentMapBinding
import com.example.gagyeboost.model.data.AccountBook
import com.example.gagyeboost.ui.base.BaseFragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : BaseFragment<FragmentMapBinding>(R.layout.fragment_map), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val tempViewModel = TempViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initMap()
    }

    private fun initObserver() {
        tempViewModel.dataMap.observe(viewLifecycleOwner, { it ->
            googleMap.clear()
            val markerMap = tempViewModel.hashMapToMarkerMap(it)
            markerMap.forEach { markerData ->
                googleMap.addMarker(
                    MarkerOptions().position(
                        LatLng(
                            markerData.key.first.toDouble(),
                            markerData.key.second.toDouble()
                        )
                    ).title(markerData.value.first).snippet(markerData.value.second)
                )?.showInfoWindow()
            }
        })
    }

    private fun initMap() {
        binding.mvMap.getMapAsync(this)
        binding.mvMap.onCreate(null)
    }

    override fun onStart() {
        super.onStart()
        binding.mvMap.onStart()
    }

    override fun onResume() {
        super.onResume()
        binding.mvMap.onResume()
    }

    override fun onStop() {
        super.onStop()
        binding.mvMap.onStop()
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        val seoul = LatLng(37.5642135, 127.0016985)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 15f))
        googleMap.setOnInfoWindowClickListener {
            //TODO Marker 위에 Info 클릭 시 해당 좌표에 대한 내역 띄워주기
            Log.i("MapFragment", it.position.toString())
        }
        tempViewModel.initMarkerData()
    }

}

//TODO test용 viewmodel 지우기
class TempViewModel() {

    // HashMap<좌표, 좌표에 해당하는 내역 list>
    private var _dataMap = MutableLiveData<HashMap<Pair<Float, Float>, ArrayList<AccountBook>>>()
    val dataMap: LiveData<HashMap<Pair<Float, Float>, ArrayList<AccountBook>>> = _dataMap

    private val testData = listOf(
        AccountBook(
            1,
            1,
            4240,
            11,
            37.57017517089844f,
            126.98320007324219f,
            "대한민국 서울특별시 종로1가 종각",
            "1",
            2021,
            11,
            7
        ),
        AccountBook(
            2,
            0,
            1330,
            1,
            37.57086181640625f,
            126.9828109741211f,
            "대한민국 서울특별시 종로구 공평동 100-5",
            "12",
            2021,
            11,
            7
        ),
        AccountBook(
            3,
            0,
            33330,
            1,
            37.57017517089844f,
            126.98320007324219f,
            "대한민국 서울특별시 종로1가 종각",
            "12",
            2021,
            11,
            6
        ),
        AccountBook(4, 0, 334550, 1, 0.0f, 0.0f, "", "12", 2021, 11, 5),
        AccountBook(
            5,
            0,
            1330,
            2,
            37.565704345703125f,
            126.97686004638672f,
            "대한민국 서울특별시 중구 지하 101 시청",
            "12",
            2021,
            11,
            5
        ),
        AccountBook(
            6,
            1,
            2550,
            13,
            37.565704345703125f,
            126.97686004638672f,
            "대한민국 서울특별시 중구 지하 101 시청",
            "12",
            2021,
            11,
            5
        ),
        AccountBook(
            7,
            0,
            660,
            2,
            37.5704345703125f,
            126.99214935302734f,
            "대한민국 서울특별시 종로3가 종로3가",
            "12",
            2021,
            11,
            4
        ),
        AccountBook(
            8,
            0,
            131110,
            3,
            37.57961654663086f,
            126.97704315185547f,
            "대한민국 서울특별시 종로구 종로1.2.3.4가동 사직로 161",
            "12",
            2021,
            11,
            10
        )
    )

    fun initMarkerData() {
        setMarkerList(testData)
    }

    //dataList에 따라 마커 재생성
    fun setMarkerList(dataList: List<AccountBook>) {
        _dataMap.value = listToHashMap(dataList)
    }

    private fun listToHashMap(dataList: List<AccountBook>): HashMap<Pair<Float, Float>, ArrayList<AccountBook>> {
        val nowMap = HashMap<Pair<Float, Float>, ArrayList<AccountBook>>()
        dataList.forEach {
            val latLng = Pair(it.latitude, it.longitude)
            nowMap.getOrPut(latLng) { ArrayList() }.add(it)
        }
        return nowMap
    }

    fun hashMapToMarkerMap(dataMap: HashMap<Pair<Float, Float>, ArrayList<AccountBook>>): HashMap<Pair<Float, Float>, Pair<String, String>> {
        val markerMap = HashMap<Pair<Float, Float>, Pair<String, String>>()
        dataMap.forEach {
            markerMap[it.key] = Pair(
                it.value[0].address,
                "${it.value.sumOf { accountBook -> accountBook.money }}원"
            )
        }
        return markerMap
    }
}