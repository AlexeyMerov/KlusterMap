package com.alexeymerov.klustermap.data.repository

import com.alexeymerov.klustermap.common.Cancelable
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

/**
 * Should extend Cancelable interface for correct CoroutineScope using.
 * */
interface PointsRepository : Cancelable {

    val isDatabaseFilled: Boolean

    val parseProgress: Flow<Int>

    fun parsePoints()

    suspend fun findPointsInBounds(northeast: LatLng, southwest: LatLng): Set<PointEntity>

}