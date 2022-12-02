package com.alexeymerov.klustermap.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.alexeymerov.klustermap.R
import com.alexeymerov.klustermap.common.KlusterRenderer
import com.alexeymerov.klustermap.common.extensions.collectWhenResumed
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.databinding.ActivityMainBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import timber.log.Timber


@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    private lateinit var map: GoogleMap

    private lateinit var clusterManager: ClusterManager<PointEntity>

    private var clusterPointsJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.fab.setOnClickListener { view ->
            viewModel.parsePoints()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map.uiSettings.isRotateGesturesEnabled = false
        map.setMinZoomPreference(1f)
        map.setMaxZoomPreference(21f)
        setUpCluster()

        map.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
            val bounds = map.projection.visibleRegion.latLngBounds
            findPoints(bounds.northeast, bounds.southwest)
        }
    }

    private fun setUpCluster() {
        clusterManager = ClusterManager(this, map)
        clusterManager.renderer = KlusterRenderer(this, map, clusterManager)
        map.setOnMarkerClickListener(clusterManager)
    }

    private fun findPoints(northeast: LatLng, southwest: LatLng) {
        clusterPointsJob?.cancel()
        clusterPointsJob = lifecycleScope.launchWhenResumed {
            delay(500)
            Timber.d("Start search")
            clusterPointsJob = viewModel.findPoints(northeast, southwest).collectWhenResumed(this@MainActivity, ::updatePointsOnMap)
        }
    }

    private fun updatePointsOnMap(points: Array<PointEntity>) {
        Timber.d("Found ${points.size}")
        clusterManager.clearItems()

        clusterManager.algorithm.lock()
        try {
            for (p in points) clusterManager.algorithm.addItem(p)

        } finally {
            clusterManager.algorithm.unlock()
        }

        clusterManager.cluster()
    }
}