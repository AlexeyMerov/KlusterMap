package com.alexeymerov.klustermap.presentation.main

import androidx.lifecycle.ViewModel
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.domain.points.PointsUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val pointsUseCase: PointsUseCase) : ViewModel() {

    fun parsePoints() = pointsUseCase.parsePoints()

    fun findPoints(northeast: LatLng, southwest: LatLng): Flow<Array<PointEntity>> {
        return pointsUseCase.findPointsInBounds(northeast, southwest)
    }

    override fun onCleared() {
        super.onCleared()
        pointsUseCase.cancelJobs()
    }

}