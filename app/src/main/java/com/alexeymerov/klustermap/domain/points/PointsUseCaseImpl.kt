package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.data.repository.PointsRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

/**
 * Do not doing anything important at the moment.
 * For demonstration needs.
 * */
class PointsUseCaseImpl @Inject constructor(private val pointsRepository: PointsRepository) : PointsUseCase, BaseCoroutineScope() {

    override val parseProgress: Flow<Int> = pointsRepository.parseProgress

    override val needShowMap: Boolean = pointsRepository.isDatabaseFilled

    override fun parsePoints() = pointsRepository.parsePoints()

    override suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Set<PointEntity> {
        Timber.d("Start search UC")
        return pointsRepository.findPointsInBounds(northeast, southwest)
    }

    override fun cancelJobs() {
        super.cancelJobs()
        pointsRepository.cancelJobs()
    }
}