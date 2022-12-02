package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.BaseCoroutineScope
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.data.repository.PointsRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class PointsUseCaseImpl @Inject constructor(private val pointsRepository: PointsRepository) : PointsUseCase, BaseCoroutineScope() {

    override fun parsePoints() = pointsRepository.parsePoints()

    override fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Flow<Array<PointEntity>> {
        return pointsRepository.findPointsInBounds(northeast, southwest)
    }

    override fun cancelJobs() {
        super.cancelJobs()
        pointsRepository.cancelJobs()
    }
}