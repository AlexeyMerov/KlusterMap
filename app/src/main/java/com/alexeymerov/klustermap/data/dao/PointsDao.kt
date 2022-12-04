package com.alexeymerov.klustermap.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Query

@Dao
abstract class PointsDao {

    @Query("SELECT * FROM points WHERE lat > :latWest AND lat < :latEast AND lon < :lonNorth AND lon > :lonSouth LIMIT 10000")
    abstract fun findPointsInBounds(latWest: Double, latEast: Double, lonNorth: Double, lonSouth: Double): Cursor

}