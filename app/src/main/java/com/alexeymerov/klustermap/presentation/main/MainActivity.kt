package com.alexeymerov.klustermap.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.alexeymerov.klustermap.R
import com.alexeymerov.klustermap.common.KlusterRenderer
import com.alexeymerov.klustermap.common.extensions.addItems
import com.alexeymerov.klustermap.common.extensions.collectWhenResumed
import com.alexeymerov.klustermap.data.entity.PointEntity
import com.alexeymerov.klustermap.databinding.ActivityMainBinding
import com.alexeymerov.klustermap.presentation.main.MainViewModel.ViewAction
import com.alexeymerov.klustermap.presentation.main.MainViewModel.ViewState
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

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var binding: ActivityMainBinding

    private lateinit var map: GoogleMap

    private lateinit var clusterManager: ClusterManager<PointEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
//        initNavController()
        initViewModel()
    }

    private fun initViews() {
        setSupportActionBar(binding.toolbar)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.fab.setOnClickListener { view ->
            sendNewAction(ViewAction.ParsePoints)
        }
    }

    private fun initNavController() {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun initViewModel() = with(viewModel) {
        viewState.collectWhenResumed(this@MainActivity, ::processViewState)
    }

    private fun processViewState(it: ViewState) {
        Timber.tag(javaClass.simpleName).d(it.javaClass.simpleName)
        if (it is ViewState.NewPointsFound) updatePointsOnMap(it.array)
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
        sendNewAction(ViewAction.FindPoints(northeast, southwest))
    }

    private fun sendNewAction(action: ViewAction) = viewModel.processAction(action)

    private fun updatePointsOnMap(points: Array<PointEntity>) {
        clusterManager.clearItems()
        clusterManager.addItems(points)
        clusterManager.cluster()
    }
}