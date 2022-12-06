package com.alexeymerov.klustermap.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class PointsDao {

    @Query(
        "SELECT * FROM points " +
            "WHERE lat < :latNorth AND lat > :latSouth " +
            "AND lon > :lonWest AND lon < :lonEast " +
            "ORDER BY random() " +
            "LIMIT 10000;"
    )
    abstract fun findPointsInBounds(latNorth: Double, latSouth: Double, lonWest: Double, lonEast: Double): Cursor
}