package com.alexeymerov.klustermap.common

import timber.log.Timber

class NumberedTimberTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String {
        return String.format(
            "$DEFAULT_TAG: (%s:%s)::%s",
            element.fileName,
            element.lineNumber,
            element.methodName
        )
    }

    companion object {
        private const val DEFAULT_TAG = "Merov"
    }
}
