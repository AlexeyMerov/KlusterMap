package com.alexeymerov.klustermap.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.klustermap.common.extensions.send
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.domain.points.PointsUseCase
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val pointsUseCase: PointsUseCase) : ViewModel() {

    private val _viewState = Channel<ViewState>()
    val viewState = _viewState.receiveAsFlow()

    private var clusterPointsJob: Job? = null

    private fun parsePoints() = pointsUseCase.parsePoints()

    private fun findPoints(northeast: LatLng, southwest: LatLng) {
        clusterPointsJob?.cancel()
        clusterPointsJob = viewModelScope.launch(Dispatchers.IO) {
            delay(500)
            Timber.d("Start search VM")
            val array = pointsUseCase.findPointsInBounds(northeast, southwest)
            Timber.d("Stop search = ${array.size}")
            setNewState(ViewState.NewPointsFound(array))
        }
    }

    override fun onCleared() {
        super.onCleared()
        pointsUseCase.cancelJobs()
    }

    private fun setNewState(state: ViewState) = _viewState.send(viewModelScope, state)

    fun processAction(action: ViewAction) {
        Timber.tag(javaClass.simpleName).d(action.javaClass.simpleName)
        when (action) {
            is ViewAction.FindPoints -> findPoints(action.northeast, action.southwest)
            ViewAction.ParsePoints -> parsePoints()
        }
    }

    sealed interface ViewState {
        class NewPointsFound(val array: Array<PointEntity>) : ViewState
    }

    sealed interface ViewAction {
        class FindPoints(val northeast: LatLng, val southwest: LatLng) : ViewAction
        object ParsePoints : ViewAction
    }

}