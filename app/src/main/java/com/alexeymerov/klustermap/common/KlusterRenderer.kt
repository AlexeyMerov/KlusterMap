package com.alexeymerov.klustermap.common

import android.content.Context
import com.alexeymerov.klustermap.R
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

/**
 * Custom realization for clusters. To use custom icons.
 * Vector images not allowed by the library.
 *
 * And to disable clusters depends on zoom level
 * */
class KlusterRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<PointEntity>) :
    DefaultClusterRenderer<PointEntity>(context, map, clusterManager) {

    private var zoomLevelForCluster = map.maxZoomLevel - (map.maxZoomLevel / 2.5) // 2.0 and 3.0 not looking good
    var currentZoomLevel: Float = 0f

    private val icon1 = BitmapDescriptorFactory.fromResource(R.drawable.whit_30)
    private val icon2 = BitmapDescriptorFactory.fromResource(R.drawable.whit_60)
    private val icon3 = BitmapDescriptorFactory.fromResource(R.drawable.whit_90)
    private val icon4 = BitmapDescriptorFactory.fromResource(R.drawable.whit_120)

    /**
     * 0 - to avoid single-markers on min zoom
     * */
    override fun shouldRenderAsCluster(cluster: Cluster<PointEntity>): Boolean {
        val needCluster = when {
            cluster.size > 1 && currentZoomLevel < zoomLevelForCluster -> true
            cluster.size > 3 -> true
            else -> false
        }

        return needCluster
    }

    override fun getDescriptorForCluster(cluster: Cluster<PointEntity>): BitmapDescriptor {
        return when {
            cluster.size >= 10000 -> icon4
            cluster.size >= 1000 -> icon3
            cluster.size >= 100 -> icon2
            else -> icon1
        }
    }

}