package com.alexeymerov.klustermap.common.extensions

import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.Algorithm

/**
 * Taken logic from inside of manager. And inspired by 'Closable.use' from Kotlin.
 * */
inline fun <T : ClusterItem> ClusterManager<T>.use(crossinline block: Algorithm<T>.() -> Unit) {
    algorithm.lock()
    try {
        block.invoke(algorithm)
    } finally {
        algorithm.unlock()
    }
}

/**
 * Has no support of Array by default. Only Collection<>.
 * */
fun <T : ClusterItem> ClusterManager<T>.addItems(array: Array<T>) {
    use {
        for (p in array) algorithm.addItem(p)
    }
}