package com.alexeymerov.klustermap.di

import android.content.Context
import com.alexeymerov.klustermap.data.KlusterDatabase
import com.alexeymerov.klustermap.data.dao.PointsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Singleton
    @Provides
    fun provideAppDB(@ApplicationContext context: Context): KlusterDatabase = KlusterDatabase.buildDatabase(context)

    @Singleton
    @Provides
    fun provideCallsDao(db: KlusterDatabase): PointsDao = db.pointsDao()

}