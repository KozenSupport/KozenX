package com.kozen.support.x.unittest

import android.content.Context
import com.kozen.financial.engine.FinancialEngine
import com.kozen.support.x.R
import com.kozen.support.x.utils.FinancialSdkErrorTranslator

object FinancialUnitTestSupport {
    const val SUCCESS = 0

    fun ensureFinancialSdkReady(
        context: Context,
        callback: UnitTestCallback,
        onReady: () -> Unit
    ) {
        if (FinancialEngine.securityManager != null &&
            FinancialEngine.pinpadManager != null &&
            FinancialEngine.printerManager != null
        ) {
            onReady()
            return
        }

        FinancialEngine.init(context.applicationContext) { code, msg ->
            if (code == SUCCESS) {
                onReady()
            } else {
                callback.onFailure(
                    context.getString(
                        R.string.unit_test_financial_init_failed,
                        msg ?: FinancialSdkErrorTranslator.describe(context, code)
                    )
                )
            }
        }
    }
}
