package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.Cancelable
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng

/**
 * Should extend Cancelable interface for correct CoroutineScope using.
 * */
interface PointsUseCase : Cancelable {

    suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Set<PointEntity>

}