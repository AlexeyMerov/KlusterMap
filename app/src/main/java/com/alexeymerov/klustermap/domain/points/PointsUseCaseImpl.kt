package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.data.repository.PointsRepository
import com.google.android.gms.maps.model.LatLngBounds
import timber.log.Timber
import javax.inject.Inject

/**
 * Do not doing anything important at the moment.
 * For demonstration needs.
 * */
class PointsUseCaseImpl @Inject constructor(private val pointsRepository: PointsRepository) : PointsUseCase, BaseCoroutineScope() {

    override suspend fun findPointsInBounds(bounds: LatLngBounds): Set<PointEntity> {
        Timber.d("Start search UC")
        return pointsRepository.findPointsInBounds(bounds)
    }

    override fun cancelJobs() {
        super.cancelJobs()
        pointsRepository.cancelJobs()
    }
}