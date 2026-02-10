package com.kozen.support.x.utils

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


/**
 * TestLogUtil
 * Records test results and exports them as a text file.
 */
object TestLogUtil {


    private val logs = mutableListOf<String>()


    /**
     * Adds a test log entry.
     */
    fun addLog(module: String, caseName: String, result: String) {
        val time = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        logs.add("[$time][$module][$caseName] $result")
    }


    /**
     * Exports logs to internal storage.
     */
    fun export(context: Context): File {
        val file = File(context.filesDir, "test_log_${System.currentTimeMillis()}.txt")
        file.writeText(logs.joinToString("--"))
        return file
    }
}