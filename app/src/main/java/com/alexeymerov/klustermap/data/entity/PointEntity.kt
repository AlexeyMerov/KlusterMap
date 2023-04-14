package com.alexeymerov.klustermap.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

/**
 * The project uses the same class for UI and DB.
 * It's not completely correct but made with an intention to avoid additional runtime actions. Like mapping.
 * */
@Entity(tableName = PointEntity.TABLE_NAME)
data class PointEntity(
    @PrimaryKey
    val id: Long,
    val lat: Double,
    val lon: Double
) : ClusterItem {

    override fun getPosition(): LatLng = LatLng(lat, lon)

    override fun getTitle(): String = id.toString()

    override fun getSnippet(): String = id.toString()

    override fun getZIndex() = 0f

    companion object {
        const val COLUMN_ID_INDEX = 0
        const val COLUMN_LAT_INDEX = 1
        const val COLUMN_LON_INDEX = 2

        const val TABLE_NAME = "points"
    }
}