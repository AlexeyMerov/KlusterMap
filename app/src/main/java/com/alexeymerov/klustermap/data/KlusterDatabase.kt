package com.alexeymerov.klustermap.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alexeymerov.klustermap.data.dao.PointsDao
import com.alexeymerov.klustermap.data.entity.PointEntity

@Database(entities = [PointEntity::class], version = 1)
abstract class KlusterDatabase : RoomDatabase() {

    abstract fun pointsDao(): PointsDao

    companion object {

        fun buildDatabase(context: Context): KlusterDatabase {
            return Room.databaseBuilder(context, KlusterDatabase::class.java, "klustermap.db")
//                .addMigrations() // in case some DB changes, don't forget to implement a migration logic
                .fallbackToDestructiveMigration()
                .build()
        }
    }

}