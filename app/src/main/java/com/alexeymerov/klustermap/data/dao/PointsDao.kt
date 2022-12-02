package com.alexeymerov.klustermap.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexeymerov.klustermap.data.entity.PointEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class PointsDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract fun insert(entity: PointEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertAll(entity: HashSet<PointEntity>)

    @Query("SELECT * FROM points")
    abstract fun getAllPoints(): Flow<List<PointEntity>>

    @Query("SELECT COUNT(id) FROM points")
    abstract suspend fun getRowCount(): Int

    @Query("SELECT * FROM points WHERE lat > :latWest AND lat < :latEast AND lon < :lonNorth AND lon > :lonSouth LIMIT 10000")
    abstract suspend fun findPointsInBounds(latWest: Double, latEast: Double, lonNorth: Double, lonSouth: Double): Array<PointEntity>

}