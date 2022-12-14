package com.alexeymerov.klustermap.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alexeymerov.klustermap.R
import com.alexeymerov.klustermap.common.KlusterRenderer
import com.alexeymerov.klustermap.common.extensions.collectWhenResumed
import com.alexeymerov.klustermap.common.extensions.use
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.databinding.ActivityMainBinding
import com.alexeymerov.klustermap.presentation.main.MainViewModel.ViewAction
import com.alexeymerov.klustermap.presentation.main.MainViewModel.ViewState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var map: GoogleMap

    private lateinit var klusterRenderer: KlusterRenderer
    private lateinit var clusterManager: ClusterManager<PointEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        initViewModel()
    }

    private fun initViewModel() {
        viewModel.viewState.collectWhenResumed(this@MainActivity, ::processViewState)
        sendNewAction(ViewAction.Initialize)
    }

    private fun processViewState(state: ViewState) {
        Timber.d(state.javaClass.simpleName)
        when (state) {
            is ViewState.NewPointsFound -> updatePointsOnMap(state.points, state.itemsToRemove)
            ViewState.ShowMap -> prepareMap()
        }
    }

    private fun prepareMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        setUpMapDefaults(googleMap)
        setupClusterManager()
    }

    private fun setUpMapDefaults(googleMap: GoogleMap) {
        val usaLatLng = CameraUpdateFactory.newLatLng(LatLng(40.0, -100.0))
        map = googleMap
        map.moveCamera(usaLatLng)
        map.uiSettings.isRotateGesturesEnabled = false // i prefer more static behaviour
        map.setMinZoomPreference(1f) // no reason. Better to eyes.
        map.setMaxZoomPreference(21f) // no reason. Better to eyes.
        map.setOnCameraIdleListener {
            handleMapIdle()
        }
    }

    private fun setupClusterManager() {
        clusterManager = ClusterManager(this, map)
        klusterRenderer = KlusterRenderer(this, map, clusterManager)
        clusterManager.renderer = klusterRenderer
        map.setOnMarkerClickListener(clusterManager) // Be aware. If you need to handle listener yourself then call cluster.markerClick manually
    }

    private fun handleMapIdle() {
        Timber.d("Zoom Level ${map.cameraPosition.zoom}")
        klusterRenderer.currentZoomLevel = map.cameraPosition.zoom
        clusterManager.onCameraIdle() // map has no addListener just replace. So need to call manually.
        val bounds = map.projection.visibleRegion.latLngBounds
        Timber.d("$bounds")
        findPoints(bounds, clusterManager.algorithm.items)
    }

    private fun findPoints(bounds: LatLngBounds, oldItems: Collection<PointEntity>) = sendNewAction(ViewAction.FindPoints(bounds, oldItems))

    private fun sendNewAction(action: ViewAction) = viewModel.processAction(action)

    /**
     * After many experiments this is the most fast-stable-readable solution i found.
     * */
    private fun updatePointsOnMap(points: Set<PointEntity>, itemsToRemove: Set<PointEntity>) {
        lifecycleScope.launch(Dispatchers.IO) {
            clusterManager.use {
                removeItems(itemsToRemove)
                addItems(points)
            }
            withContext(Dispatchers.Main) {
                clusterManager.cluster()
            }
        }
    }
}