package com.alexeymerov.klustermap

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

abstract class BaseCoroutineScope(private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : CoroutineScope {

    private val supervisorJob = SupervisorJob()

    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Timber.e(throwable, "Class: ${this::class.java.simpleName}\nCoroutineContext: $coroutineContext")
    }

    private val coroutineName = CoroutineName(this::class.java.simpleName)

    override val coroutineContext: CoroutineContext
        get() = dispatcher + supervisorJob + exceptionHandler + coroutineName

    fun cancelJobs() = supervisorJob.cancel()
}