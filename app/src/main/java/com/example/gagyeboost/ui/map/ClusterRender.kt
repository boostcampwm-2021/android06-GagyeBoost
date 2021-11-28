package com.example.gagyeboost.ui.map

import android.content.Context
import androidx.core.content.res.ResourcesCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.gagyeboost.R
import com.example.gagyeboost.common.BitmapUtils
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.data.MyItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.geometry.Bounds
import com.google.maps.android.ui.IconGenerator

class MyClusterRenderer(
    val context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<MyItem?>,
    private val isExpense: LiveData<Int>
) : DefaultClusterRenderer<MyItem>(context, map, clusterManager) {

    private val clusterIconGenerator = IconGenerator(context)
    private var _markerBound = MutableLiveData(Bounds(0.0, 0.0, 0.0, 0.0))
    val markerBound: LiveData<Bounds> = _markerBound

    companion object {
        private const val MAX_BOUND = 3.0
        private const val MINIMUM_BOUND_SIZE = 1 / (MAX_BOUND * MAX_BOUND)
    }

    override fun onBeforeClusterItemRendered(item: MyItem, markerOptions: MarkerOptions) {
        ResourcesCompat.getDrawable(
            context.resources,
            if (isExpense.value == 0) R.drawable.ic_expense_marker else R.drawable.ic_income_marker,
            null
        )?.let {
            val bitmap = BitmapUtils.createBitmapFromDrawable(it)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap))
        }
    }

    override fun onBeforeClusterRendered(cluster: Cluster<MyItem?>, markerOptions: MarkerOptions) {

        clusterIconGenerator.setContentPadding(50, 50, 50, 50)

        var totalMoney = 0
        cluster.items.forEach {
            it?.snippet?.let { money ->
                totalMoney += money.replace("원", "").toInt()
            }
        }

        val icon = clusterIconGenerator.makeIcon("${formatter.format(totalMoney)}원")
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon))
    }

    override fun onClusterItemRendered(clusterItem: MyItem, marker: Marker) {

        super.onClusterItemRendered(clusterItem, marker.apply {
            title = clusterItem.title
            snippet = clusterItem.snippet
        })


    }

    override fun onClusterUpdated(cluster: Cluster<MyItem>, marker: Marker) = Unit

    /*
     * 카메라 Bound값을 넣고 현재 Renderer Bound안에 있는지, Bound크기가 너무 작은지 검사
     * 조건을 만족하지 않으면 Bound 재조정
     */
    fun resizeBound(minX: Double, maxX: Double, minY: Double, maxY: Double) {
        if (isInBound(minX, maxX, minY, maxY) && isLargerThanMinSize(minX, maxX, minY, maxY)) return
        val xSize = maxX - minX
        val ySize = maxY - minY
        val xCenter = (maxX + minX) / 2
        val yCenter = (maxY + minY) / 2
        _markerBound.value =
            Bounds(
                xCenter - xSize * MAX_BOUND / 2,
                xCenter + xSize * MAX_BOUND / 2,
                yCenter - ySize * MAX_BOUND / 2,
                yCenter + ySize * MAX_BOUND / 2
            )
    }

    private fun isInBound(minX: Double, maxX: Double, minY: Double, maxY: Double) =
        markerBound.value?.let {
            minX >= it.minX && minY >= it.minY && maxX <= it.maxX && maxY <= it.maxY
        } ?: false

    private fun isInBound(x: Double, y: Double) = markerBound.value?.run {
        x in (minX..maxX) && y in (minY..maxY)
    } ?: false

    private fun isLargerThanMinSize(minX: Double, maxX: Double, minY: Double, maxY: Double) =
        markerBound.value?.let {
            val xSize = maxX - minX
            val ySize = maxY - minY
            val boundXSize = it.maxX - it.minX
            val boundYSize = it.maxY - it.minY
            boundXSize * MINIMUM_BOUND_SIZE <= xSize && boundYSize * MINIMUM_BOUND_SIZE <= ySize
        } ?: true

    fun addInBoundMarker(clusterManager: ClusterManager<MyItem?>, offsetItem: MyItem) {
        offsetItem.position.run {
            if (isInBound(latitude, longitude)) clusterManager.addItem(offsetItem)
        }
    }
}
