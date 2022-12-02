package com.alexeymerov.klustermap.data.repository

import com.alexeymerov.klustermap.common.Cancelable
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

interface PointsRepository : Cancelable {

    fun parsePoints()

    fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Flow<Array<PointEntity>>

}