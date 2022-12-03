package com.alexeymerov.klustermap

import android.app.Application
import com.alexeymerov.klustermap.common.NumberedTimberTree
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class KlusterApp : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) Timber.plant(NumberedTimberTree())
    }

}