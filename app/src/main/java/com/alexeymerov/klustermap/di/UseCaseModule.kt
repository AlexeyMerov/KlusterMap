package com.alexeymerov.klustermap.di

import com.alexeymerov.klustermap.domain.points.PointsUseCase
import com.alexeymerov.klustermap.domain.points.PointsUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindPointsUseCase(pointsUseCase: PointsUseCaseImpl): PointsUseCase

}