package com.alexeymerov.klustermap.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class PointsDao {

    /**
     * Dividing our big bound by 4 smaller ones to disperse points.
     * Additionally using random() for same purpose but inside smaller zone.
     * UNION ALL but not UNION since we don't need an uniqueness.
     *
     * Without this query it's also possible, just make 4 calls from outside but will add more boilerplate.
     * */
    @Query("SELECT * FROM (SELECT * FROM points WHERE (lat BETWEEN :latCenter AND :latNorth) AND (lon BETWEEN :lonWest AND :lonCenter) ORDER BY random() LIMIT 2500) " +
               "UNION ALL " +
               "SELECT * FROM (SELECT * FROM points WHERE (lat BETWEEN :latCenter AND :latNorth) AND (lon BETWEEN :lonCenter AND :lonEast) ORDER BY random() LIMIT 2500) " +
               "UNION ALL " +
               "SELECT * FROM (SELECT * FROM points WHERE (lat BETWEEN :latSouth AND :latCenter) AND (lon BETWEEN :lonWest AND :lonCenter) ORDER BY random() LIMIT 2500) " +
               "UNION ALL " +
               "SELECT * FROM (SELECT * FROM points WHERE (lat BETWEEN :latSouth AND :latCenter) AND (lon BETWEEN :lonCenter AND :lonEast) ORDER BY random() LIMIT 2500)")
    abstract fun findPointsInBounds(
        latNorth: Double,
        latSouth: Double,
        lonWest: Double,
        lonEast: Double,
        latCenter: Double,
        lonCenter: Double
    ): Cursor
}