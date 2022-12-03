package com.alexeymerov.klustermap.di

import com.alexeymerov.klustermap.data.repository.PointsRepository
import com.alexeymerov.klustermap.data.repository.PointsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindRepositoryModule(repositoryModule: PointsRepositoryImpl): PointsRepository

}