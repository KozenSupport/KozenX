package com.kozen.support.x.unittest

import android.content.Context
import android.os.Bundle
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager.EmvPinConstraints
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.pinpad.PinpadInputCallback
import com.kozen.financial.util.emv.tlv.HexUtil
import com.kozen.support.x.R
import com.kozen.support.x.config.KeyIndexConstant
import com.kozen.support.x.utils.FinancialSdkErrorTranslator

sealed class OnlinePinUnitTest(
    private val keyMode: Int,
    private val keyIndex: Int
) : UnitTestCase {
    override val keepDialogVisibleDuringRun: Boolean = false

    object Tpk : OnlinePinUnitTest(
        EmvPinConstraints.PIN_KEY_MODE_TPK,
        KeyIndexConstant.TPK_TDES_DATA_INDEX
    ) {
        override fun runningMessage(context: Context): String {
            return context.getString(R.string.unit_test_pin_tpk_running)
        }

        override fun successMessage(context: Context): String {
            return context.getString(R.string.unit_test_pin_tpk_success)
        }
    }

    object Dukpt : OnlinePinUnitTest(
        EmvPinConstraints.PIN_KEY_MODE_DUKPT,
        KeyIndexConstant.DUKPT_TDES_TIK_INDEX
    ) {
        override fun runningMessage(context: Context): String {
            return context.getString(R.string.unit_test_pin_dukpt_running)
        }

        override fun successMessage(context: Context): String {
            return context.getString(R.string.unit_test_pin_dukpt_success)
        }
    }

    override fun start(context: Context, callback: UnitTestCallback) {
        FinancialUnitTestSupport.ensureFinancialSdkReady(context, callback) {
            val pinpadManager = FinancialEngine.pinpadManager
            if (pinpadManager == null) {
                callback.onFailure(context.getString(R.string.unit_test_pin_manager_missing))
                return@ensureFinancialSdkReady
            }

            pinpadManager.startInputPin(buildPinParams(), object : PinpadInputCallback {
                override fun onInput(len: Int, key: Int) = Unit

                override fun onPinSuccess(verifyResult: Int, pinBlock: ByteArray, ksn: String?) {
                    if (pinBlock.isEmpty()) {
                        callback.onFailure(context.getString(R.string.unit_test_pin_empty_block))
                        return
                    }
                    callback.onSuccess()
                }

                override fun onPinError(verifyResult: Int, pinTryCntOut: Int) {
                    FinancialEngine.pinpadManager?.cancelInputPin()
                    callback.onFailure(
                        context.getString(R.string.unit_test_pin_failed, verifyResult, pinTryCntOut) +
                            "\n" + FinancialSdkErrorTranslator.describe(context, verifyResult)
                    )
                }

                override fun onScreenRotation() = Unit
            })
        }
    }

    private fun buildPinParams(): Bundle {
        return Bundle().apply {
            putInt(EmvPinConstraints.PIN_TYPE, POIEmvCoreManager.PIN_ONLINE_PIN)
            putByteArray(EmvPinConstraints.PIN_LENGTH_LIMIT, byteArrayOf(4, 5, 6, 7, 8, 9, 10, 11, 12))
            putInt(EmvPinConstraints.PIN_TIMEOUT, 60)
            putString(EmvPinConstraints.PIN_CARD, "6217680206725660")
            putInt(EmvPinConstraints.PIN_KEY_MODE, keyMode)
            putInt(EmvPinConstraints.PIN_KEY_ALGORITHM, EmvPinConstraints.PIN_KEY_ALGORITHM_TDES)
            putInt(EmvPinConstraints.PIN_KEY_INDEX, keyIndex)
            putInt(EmvPinConstraints.PIN_BLOCK_FORMAT, EmvPinConstraints.PIN_ISO_FMT0)
            putBoolean(EmvPinConstraints.PIN_IS_ORDER, false)
            putBoolean(EmvPinConstraints.PIN_BYPASS, false)
            putBoolean(EmvPinConstraints.PIN_BYPASS_ALL_TYPE, false)
            putByteArray(EmvPinConstraints.PIN_CARD_RANDOM, HexUtil.parseHex("A1A2A3A4A5A6A7A8"))
        }
    }
}
