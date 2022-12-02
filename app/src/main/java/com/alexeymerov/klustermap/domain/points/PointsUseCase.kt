package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.Cancelable
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng

interface PointsUseCase : Cancelable {

    fun parsePoints()

    suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Array<PointEntity>

}