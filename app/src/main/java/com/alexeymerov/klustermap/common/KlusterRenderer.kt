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
 * Custom realization for clusters. At the moment just to use custom icons.
 * And clusterize 2 or more points (default is 4).
 *
 * Vector images not allowed by the library.
 * */
class KlusterRenderer(context: Context, map: GoogleMap, clusterManager: ClusterManager<PointEntity>) :
    DefaultClusterRenderer<PointEntity>(context, map, clusterManager) {

    private val icon1 = BitmapDescriptorFactory.fromResource(R.drawable.whit_30)
    private val icon2 = BitmapDescriptorFactory.fromResource(R.drawable.whit_60)
    private val icon3 = BitmapDescriptorFactory.fromResource(R.drawable.whit_90)
    private val icon4 = BitmapDescriptorFactory.fromResource(R.drawable.whit_120)

    override fun shouldRenderAsCluster(cluster: Cluster<PointEntity>): Boolean = cluster.size > 1

    override fun getDescriptorForCluster(cluster: Cluster<PointEntity>): BitmapDescriptor {
        return when {
            cluster.size >= 10000 -> icon4
            cluster.size >= 1000 -> icon3
            cluster.size >= 100 -> icon2
            else -> icon1
        }
    }

}