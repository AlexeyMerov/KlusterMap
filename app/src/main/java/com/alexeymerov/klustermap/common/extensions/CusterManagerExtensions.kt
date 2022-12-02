package com.alexeymerov.klustermap.common.extensions

import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager

fun <T : ClusterItem> ClusterManager<T>.addItems(array: Array<T>) {
    algorithm.lock()
    try {
        for (p in array) algorithm.addItem(p)
    } finally {
        algorithm.unlock()
    }
}