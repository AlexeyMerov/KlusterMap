package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.data.repository.PointsRepository
import com.google.android.gms.maps.model.LatLng
import timber.log.Timber
import javax.inject.Inject

class PointsUseCaseImpl @Inject constructor(private val pointsRepository: PointsRepository) : PointsUseCase, BaseCoroutineScope() {

    override fun parsePoints() = pointsRepository.parsePoints()

    override suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Array<PointEntity> {
        Timber.d("Start search UC")
        return pointsRepository.findPointsInBounds(northeast, southwest)
    }

    override fun cancelJobs() {
        super.cancelJobs()
        pointsRepository.cancelJobs()
    }
}