package com.alexeymerov.klustermap.domain.points

import com.alexeymerov.klustermap.common.Cancelable
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface PointsUseCase : Cancelable {

    fun parsePoints()

    fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Flow<Array<PointEntity>>

}