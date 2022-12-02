package com.alexeymerov.klustermap.data.repository

import com.alexeymerov.klustermap.common.Cancelable
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng

interface PointsRepository : Cancelable {

    fun parsePoints()

    suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Array<PointEntity>

}