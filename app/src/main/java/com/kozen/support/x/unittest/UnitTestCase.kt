package com.kozen.support.x.unittest

import android.content.Context

interface UnitTestCase {
    fun runningMessage(context: Context): String
    fun successMessage(context: Context): String
    fun start(context: Context, callback: UnitTestCallback)
}

interface UnitTestCallback {
    fun onSuccess()
    fun onFailure(message: String)
}
