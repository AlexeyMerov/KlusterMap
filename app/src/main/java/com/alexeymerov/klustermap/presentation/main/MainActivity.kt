package com.alexeymerov.klustermap.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
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
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var binding: ActivityMainBinding

    private lateinit var map: GoogleMap

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
            is ViewState.NewPointsFound -> updatePointsOnMap(state.points)
            ViewState.FirstInit -> prepareFirstInit()
            ViewState.ShowMap -> prepareMap()
        }
    }

    private fun prepareFirstInit() {
        binding.textView.isVisible = true
        binding.buttonView.isVisible = true
        binding.buttonView.setOnClickListener {
            binding.buttonView.isEnabled = false
            sendNewAction(ViewAction.ParsePoints)
        }
        viewModel.parseProgress.collectWhenResumed(this@MainActivity) {
            binding.buttonView.text = it.toString()
        }
    }

    private fun prepareMap() {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.textView.isVisible = false
        binding.buttonView.isVisible = false
        binding.map.isVisible = true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        val usaLatLng = CameraUpdateFactory.newLatLng(LatLng(40.0, -100.0))
        map = googleMap
        map.moveCamera(usaLatLng)
        map.uiSettings.isRotateGesturesEnabled = false // i prefer more static behaviour
        map.setMinZoomPreference(1f) // no reason. Better to eyes.
        map.setMaxZoomPreference(21f) // no reason. Better to eyes.
        setUpCluster()

        map.setOnCameraIdleListener {
            clusterManager.onCameraIdle() // map has no addListener just replace. So need to call manually.
            val bounds = map.projection.visibleRegion.latLngBounds
            findPoints(bounds.northeast, bounds.southwest)
        }
    }

    private fun setUpCluster() {
        clusterManager = ClusterManager(this, map)
        clusterManager.renderer = KlusterRenderer(this, map, clusterManager)
        map.setOnMarkerClickListener(clusterManager) // Be aware. If you need to handle listener yourself then call cluster.markerClick manually
    }

    private fun findPoints(northeast: LatLng, southwest: LatLng) = sendNewAction(ViewAction.FindPoints(northeast, southwest))

    private fun sendNewAction(action: ViewAction) = viewModel.processAction(action)

    /**
     * After many experiments this is the most fast-stable-readable solution i found.
     * */
    private fun updatePointsOnMap(points: Set<PointEntity>) {
        clusterManager.use {
            clearItems()
            addItems(points)
        }
        clusterManager.cluster()
    }
}