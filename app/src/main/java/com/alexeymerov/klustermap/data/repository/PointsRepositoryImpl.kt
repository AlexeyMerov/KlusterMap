package com.alexeymerov.klustermap.data.repository

import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.dao.PointsDao
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLngBounds
import timber.log.Timber
import javax.inject.Inject

/**
 * For all the LatLng points processing.
 *
 * Originally only to parse from CSV and finding Set in the bounds.
 * */
class PointsRepositoryImpl @Inject constructor(
    private val pointsDao: PointsDao
) : PointsRepository, BaseCoroutineScope() {

    /**
     * Find point in the DB by the bounds.
     * The cleaner solution would be with using Array.
     * But Cluster works with Collection interface so we make Set ourselves.
     *
     * center - to evenly spread point by 4 zones
     * */
    override suspend fun findPointsInBounds(bounds: LatLngBounds): Set<PointEntity> {
        Timber.d("Start search RP")
        val result = HashSet<PointEntity>()

        val points = pointsDao.findPointsInBounds(
            latNorth = bounds.northeast.latitude,
            latSouth = bounds.southwest.latitude,
            lonWest = bounds.southwest.longitude,
            lonEast = bounds.northeast.longitude,
            latCenter = bounds.center.latitude,
            lonCenter = bounds.center.longitude
        )

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

}