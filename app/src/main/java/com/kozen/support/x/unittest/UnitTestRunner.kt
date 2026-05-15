package com.kozen.support.x.unittest

import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.R

object UnitTestRunner {
    private const val SUCCESS_HOLD_MS = 800L

    private val mainHandler = Handler(Looper.getMainLooper())

    @Volatile
    private var isRunning = false

    fun run(activity: AppCompatActivity, tests: List<UnitTestCase>) {
        if (tests.isEmpty()) {
            showFinalDialog(activity, activity.getString(R.string.unit_test_no_cases))
            return
        }
        if (isRunning) {
            showFinalDialog(activity, activity.getString(R.string.unit_test_already_running))
            return
        }

        isRunning = true
        Session(activity, tests).start()
    }

    private fun markFinished() {
        isRunning = false
    }

    private fun showFinalDialog(activity: AppCompatActivity, message: String) {
        if (activity.isFinishing || activity.isDestroyed) return
        AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.unit_test_title))
            .setMessage(message)
            .setPositiveButton(activity.getString(R.string.common_ok), null)
            .show()
    }

    private class Session(
        private val activity: AppCompatActivity,
        private val tests: List<UnitTestCase>
    ) {
        private var dialog: AlertDialog? = null

        fun start() {
            runNext(0)
        }

        private fun runNext(index: Int) {
            if (!isActivityAlive()) {
                markFinished()
                return
            }

            val test = tests[index]
            showProgress(test.runningMessage(activity))
            val startBlock = {
                if (!test.keepDialogVisibleDuringRun) {
                    dialog?.dismiss()
                }
                test.start(activity, object : UnitTestCallback {
                override fun onSuccess() {
                    mainHandler.post {
                        if (!isActivityAlive()) {
                            markFinished()
                            return@post
                        }

                        showProgress(test.successMessage(activity))
                        if (index + 1 < tests.size) {
                            mainHandler.postDelayed({ runNext(index + 1) }, SUCCESS_HOLD_MS)
                        } else {
                            showComplete(test.successMessage(activity))
                        }
                    }
                }

                override fun onFailure(message: String) {
                    mainHandler.post {
                        markFinished()
                        showComplete(activity.getString(R.string.unit_test_failed, message))
                    }
                }
                })
            }

            if (test.keepDialogVisibleDuringRun) {
                startBlock()
            } else {
                mainHandler.postDelayed(startBlock, SUCCESS_HOLD_MS)
            }
        }

        private fun showProgress(message: String) {
            val currentDialog = dialog
            if (currentDialog?.isShowing == true) {
                currentDialog.setTitle(activity.getString(R.string.unit_test_title))
                currentDialog.setMessage(message)
                return
            }

            dialog = AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.unit_test_title))
                .setMessage(message)
                .setCancelable(false)
                .create()
                .also { it.show() }
        }

        private fun showComplete(message: String) {
            markFinished()
            dialog?.dismiss()
            dialog = null
            if (!isActivityAlive()) return

            dialog = AlertDialog.Builder(activity)
                .setTitle(activity.getString(R.string.unit_test_title))
                .setMessage(message)
                .setPositiveButton(activity.getString(R.string.common_ok), null)
                .create()
                .also { it.show() }
        }

        private fun isActivityAlive(): Boolean {
            return !activity.isFinishing && !activity.isDestroyed
        }
    }
}
