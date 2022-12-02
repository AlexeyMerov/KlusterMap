package com.alexeymerov.klustermap.data.repository

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.core.content.edit
import com.alexeymerov.klustermap.R
import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.dao.PointsDao
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import javax.inject.Inject

class PointsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources,
    private val pointsDao: PointsDao
) : PointsRepository, BaseCoroutineScope() {

    override fun parsePoints() {
        launch {
            val yes = sharedPreferences.getBoolean(KEY_DB_FILLED, false)
            if (yes) return@launch

            val set = resources.openRawResource(R.raw.hotspots)
                .reader()
                .buffered(39)
                .use(::parsePoints)

            pointsDao.insertAll(set)

            sharedPreferences.edit {
                putBoolean(KEY_DB_FILLED, true)
            }
            Timber.d("Items in DB = ${pointsDao.getRowCount()}")
        }
    }

    private fun parsePoints(reader: BufferedReader): HashSet<PointEntity> {
        var items: List<String>
        var id: String
        var lat: String
        var lon: String

        return reader
            .lineSequence()
            .drop(1)
            .mapIndexedNotNull { i, it ->
                if (i % 100000 == 0) Timber.d("$i")

                items = it.substring(it.indexOf(',') + 1).split(",")
                id = items[0]
                lat = items[1]
                lon = items[2]

                if (lat.isNotEmpty() && lon.isNotEmpty()) {
                    PointEntity(id.toLong(), lat.toDouble(), lon.toDouble())
                } else {
                    null
                }
            }
            .toHashSet()
    }

    override fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Flow<Array<PointEntity>> {
        return pointsDao.findPointsInBounds(
            latWest = southwest.latitude,
            latEast = northeast.latitude,
            lonNorth = northeast.longitude,
            lonSouth = southwest.longitude)
    }

    private companion object {
        const val KEY_DB_FILLED = "key_db_filled"
    }
}