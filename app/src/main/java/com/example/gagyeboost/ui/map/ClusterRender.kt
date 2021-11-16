package com.example.gagyeboost.ui.map

import android.content.Context
import com.example.gagyeboost.common.formatter
import com.example.gagyeboost.model.data.MyItem
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.ui.IconGenerator


class MyClusterRenderer(
    val context: Context?,
    map: GoogleMap?,
    clusterManager: ClusterManager<MyItem?>
) : DefaultClusterRenderer<MyItem>(context, map, clusterManager) {

    private val clusterIconGenerator = IconGenerator(context)

    override fun onBeforeClusterItemRendered(item: MyItem, markerOptions: MarkerOptions) {
        val markerDescriptor = BitmapDescriptorFactory.defaultMarker()
        markerOptions.icon(markerDescriptor)
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
}
