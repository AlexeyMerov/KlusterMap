package com.alexeymerov.klustermap.data.repository

import android.content.SharedPreferences
import android.content.res.Resources
import androidx.core.content.edit
import com.alexeymerov.klustermap.R
import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.dao.PointsDao
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.BufferedReader
import javax.inject.Inject

/**
 * For all the LatLng points processing.
 *
 * Originally only to parse from CSV and finding Set in the bounds.
 * */
class PointsRepositoryImpl @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val resources: Resources,
    private val pointsDao: PointsDao
) : PointsRepository, BaseCoroutineScope() {

    /**
     * Parsing points from integrated CSV table.
     * Perfectly this data should be parted in some way,
     * but request was to work with the file as it is.
     *
     * SharedPrefs looks more decoupled solution than check if DB isNotEmpty.
     * The parsing could be interrupted.
     *
     * Prefilled DB maybe could be better,
     * but with this amount of data, it's better to control it in the code, not in the DB callback.
     * Plus convert .csv to .db is kinda hell.
     *
     * Open DB directly to execSQL has no performance improvements.
     * From couple devices 1.5m items parsed in ~1 minute.
     * */
    override fun parsePoints() {
        launch {
            val isDbFilled = sharedPreferences.getBoolean(KEY_DB_FILLED, false)
            if (isDbFilled) return@launch

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

    /**
     * Was simplified for the glory of optimization.
     * More readable solution is to use separate collection operators,
     * but after some time checks current is the optimal enough.
     *
     * reader.readLine() makes not difference to speed in my tests.
     * */
    private fun parsePoints(reader: BufferedReader): Set<PointEntity> {
        var items: List<String>
        var id: String // doesn't make much sense. But good for readability
        var lat: String
        var lon: String

        return reader
            .lineSequence()
            .drop(1)
            .mapIndexedNotNull { i, it ->
                if (i % 100000 == 0) Timber.d("$i") // to log the process. Although room.insert is the most time consuming.

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

    /**
     * Find point in the DB by the bounds.
     * The cleaner solution would be with using Array.
     * But Cluster works with Collection interface so we make Set ourselves.
     * */
    override suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Set<PointEntity> {
        Timber.d("Start search RP")
        val points = pointsDao.findPointsInBounds(
            latWest = southwest.latitude,
            latEast = northeast.latitude,
            lonNorth = northeast.longitude,
            lonSouth = southwest.longitude)

        val result = HashSet<PointEntity>()
        for (i in 0 until points.count) {
            points.moveToNext()
            result.add(PointEntity(
                id = points.getLong(PointEntity.COLUMN_ID_INDEX),
                lat = points.getDouble(PointEntity.COLUMN_LAT_INDEX),
                lon = points.getDouble(PointEntity.COLUMN_LON_INDEX)))
        }
        points.close() // feel free to change to 'use {}' extension

        return result

    }

    private companion object {
        const val KEY_DB_FILLED = "key_db_filled"
    }
}