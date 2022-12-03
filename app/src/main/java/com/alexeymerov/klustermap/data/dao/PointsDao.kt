package com.alexeymerov.klustermap.data.dao

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.klustermap.data.entity.PointEntity

@Dao
abstract class PointsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAll(entity: Set<PointEntity>)

    @Query("SELECT COUNT(id) FROM points")
    abstract suspend fun getRowCount(): Int

    @Query("SELECT * FROM points WHERE lat > :latWest AND lat < :latEast AND lon < :lonNorth AND lon > :lonSouth LIMIT 10000")
    abstract fun findPointsInBounds(latWest: Double, latEast: Double, lonNorth: Double, lonSouth: Double): Cursor

}