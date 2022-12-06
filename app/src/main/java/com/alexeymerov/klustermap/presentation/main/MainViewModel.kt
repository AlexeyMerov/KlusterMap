package com.alexeymerov.klustermap.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alexeymerov.klustermap.common.extensions.send
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.domain.points.PointsUseCase
import com.google.android.gms.maps.model.LatLngBounds
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

    /** Channel is more suitable in my head and experience then StateFlow */
    private val _viewState = Channel<ViewState>(Channel.CONFLATED)
    val viewState = _viewState.receiveAsFlow()

    /** To use debounce for reduce the amount of searches */
    private var clusterPointsJob: Job? = null

    private fun init() {
        val state = ViewState.ShowMap
        setNewState(state)
    }

    private fun findPoints(bounds: LatLngBounds) {
        clusterPointsJob?.cancel()
        clusterPointsJob = viewModelScope.launch(Dispatchers.IO) {
            delay(250) // some debounce for reduce the amount of searches
            Timber.d("Start search VM")
            val points = pointsUseCase.findPointsInBounds(bounds)
            Timber.d("Stop search = ${points.size}")
            setNewState(ViewState.NewPointsFound(points))
        }
    }

    override fun onCleared() {
        super.onCleared()
        pointsUseCase.cancelJobs()
    }

    private fun setNewState(state: ViewState) = _viewState.send(viewModelScope, state)

    fun processAction(action: ViewAction) {
        Timber.d(action.javaClass.simpleName)
        when (action) {
            is ViewAction.FindPoints -> findPoints(action.bounds)
            ViewAction.Initialize -> init()
        }
    }

    sealed interface ViewState {
        object ShowMap : ViewState
        class NewPointsFound(val points: Set<PointEntity>) : ViewState
    }

    sealed interface ViewAction {
        object Initialize : ViewAction
        class FindPoints(val bounds: LatLngBounds) : ViewAction
    }

}