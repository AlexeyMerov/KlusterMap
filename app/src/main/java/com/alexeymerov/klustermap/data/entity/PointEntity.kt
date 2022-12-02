package com.alexeymerov.klustermap.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

@Entity(tableName = "points")
data class PointEntity(
    @PrimaryKey
    val id: Long,
    val lat: Double,
    val lon: Double
) : ClusterItem {

    override fun getPosition(): LatLng = LatLng(lat, lon)

    override fun getTitle(): String = id.toString()

    override fun getSnippet(): String = id.toString()
}