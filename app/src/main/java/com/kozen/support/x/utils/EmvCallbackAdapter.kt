package com.kozen.support.x.utils

import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.kozen.financial.constant.ConstantCardReader
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager
import com.kozen.financial.constant.ConstantEmv.PosEmvErrorCode
import com.kozen.financial.emv.IEmvListener
import com.kozen.financial.emv.IEmvManager
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.util.emv.tlv.BerTag
import com.kozen.financial.util.emv.tlv.BerTlv
import com.kozen.financial.util.emv.tlv.BerTlvParser
import com.kozen.support.x.config.DeviceConfig
import com.kozen.support.x.model.TransactionData
import com.kozen.support.x.ui.PaymentActivity
import com.kozen.support.x.ui.PinInputDialog
import java.nio.charset.StandardCharsets

class EmvCallbackAdapter(
    private val transData: TransactionData,
    private val emvManager: IEmvManager,
    private val activity: PaymentActivity,
    private val updateProgress: (msg: String) -> Unit,
    private val updateResult: (code: Int, msg: String) -> Unit
) : IEmvListener {

    private var currentPinDialog: PinInputDialog? = null
    private var pinFlowEndedByApp = false
    private var lastStage = "Waiting for card"

    override fun onEmvProcess(type: Int, bundle: Bundle) {
        Log.d(TAG, "onEmvProcess type=$type info=${Gson().toJson(bundle)}")
        when (type) {
            POIEmvCoreManager.DEVICE_CONTACT -> reportProgress("Processing contact card...")
            POIEmvCoreManager.DEVICE_CONTACTLESS -> reportProgress("Processing contactless card...")
            POIEmvCoreManager.DEVICE_MAGSTRIPE -> {
                transData.cardType = type
                reportProgress(captureMagstripeInfo(bundle))
            }
            PosEmvErrorCode.EMV_MULTI_CONTACTLESS -> updateResult(-1, "Multiple cards detected.")
            else -> reportProgress("Processing card...")
        }
    }

    override fun onSelectApplication(appList: MutableList<String>, isFirstSelect: Boolean) {
        Log.d(TAG, "onSelectApplication count=${appList.size}, first=$isFirstSelect")
        if (appList.isEmpty()) {
            emvManager.stopTransaction()
            updateResult(-1, "No application found on card.")
            return
        }

        logApplicationList(appList)
        val ret = emvManager.setSelectApplicationResponse(0)
        if (ret != 0) {
            emvManager.stopTransaction()
            updateResult(-1, "Select AID failed: $ret")
        } else {
            reportProgress("Application selected.")
        }
    }

    override fun onConfirmCardInfo(mode: Int, info: Bundle) {
        Log.d(TAG, "onConfirmCardInfo mode=$mode info=${Gson().toJson(info)}")
        reportProgress("Confirming card info...")

        if (DeviceConfig.pinByPass) {
            emvManager.setKernel(ByteUtils.hexStringToBytes("DF31020001"))
        } else {
            emvManager.setKernel(ByteUtils.hexStringToBytes("DF31020000"))
        }

        val outBundle = Bundle()
        when (mode) {
            POIEmvCoreManager.CMD_CARD_READ_SUCCESS -> {
                reportProgress("Card read successfully.")
                outBundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true)
            }
            POIEmvCoreManager.CMD_AMOUNT_CONFIG -> {
                outBundle.putString(POIEmvCoreManager.EmvCardInfoConstraints.OUT_AMOUNT, transData.transAmount.toString())
                outBundle.putString(POIEmvCoreManager.EmvCardInfoConstraints.OUT_AMOUNT_OTHER, "0")
            }
            POIEmvCoreManager.CMD_SELECT_APPLICATION,
            POIEmvCoreManager.CMD_READ_RECORD,
            POIEmvCoreManager.CMD_SELECT_AFTER -> {
                outBundle.putByteArray(POIEmvCoreManager.EmvCardInfoConstraints.OUT_TLV, ByteArray(0))
                if (mode == POIEmvCoreManager.CMD_READ_RECORD && hasMagstripeTrack(info)) {
                    reportProgress(captureMagstripeInfo(info))
                }
            }
            else -> {
                outBundle.putBoolean(POIEmvCoreManager.EmvCardInfoConstraints.OUT_CONFIRM, true)
            }
        }
        Log.d(TAG, "setCardInfoResponse mode=$mode response=${Gson().toJson(outBundle)}")
        emvManager.setCardInfoResponse(outBundle)
    }

    override fun onKernelType(type: Int) {
        transData.cardType = type
        Log.d(TAG, "onKernelType type=$type")
    }

    override fun onSecondTapCard() {
        reportProgress("Please tap card again.")
    }

    override fun onRequestInputPin(info: Bundle) {
        Log.d(TAG, "onRequestInputPin info=${Gson().toJson(info)}")
        reportProgress("Please enter PIN...")
        activity.runOnUiThread {
            currentPinDialog = PinInputDialog(activity, info).apply {
                setPinInputCallback(object : PinInputDialog.PinInputCallback {
                    override fun onPinSuccess(verifyResult: Int, pinBlock: ByteArray, ksn: String?) {
                        val response = Bundle().apply {
                            putInt(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, verifyResult)
                            putByteArray(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_BLOCK, pinBlock)
                        }
                        Log.d(TAG, "PIN input success, ksn=$ksn")
                        emvManager.setPinResponse(response)
                    }

                    override fun onPinError(verifyResult: Int, pinTryCntOut: Int) {
                        Log.w(TAG, "PIN error result=$verifyResult try=$pinTryCntOut")
                        if (verifyResult == POIEmvCoreManager.EmvPinConstraints.VERIFY_CANCELED ||
                            verifyResult == POIEmvCoreManager.EmvPinConstraints.VERIFY_MANUALLY_CANCELED
                        ) {
                            pinFlowEndedByApp = true
                            emvManager.stopTransaction()
                            updateResult(-1, "PIN canceled (verifyResult=$verifyResult)")
                            return
                        }
                        if (verifyResult == POIEmvCoreManager.EmvPinConstraints.VERIFY_TIMEOUT) {
                            pinFlowEndedByApp = true
                            emvManager.stopTransaction()
                            updateResult(-1, "PIN timeout")
                            return
                        }
                        if (verifyResult < 0) {
                            pinFlowEndedByApp = true
                            emvManager.stopTransaction()
                            updateResult(-1, "PIN input failed: $verifyResult")
                            return
                        }
                        val response = Bundle().apply {
                            putInt(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_VERIFY_RESULT, verifyResult)
                            putInt(POIEmvCoreManager.EmvPinConstraints.OUT_PIN_TRY_COUNTER, pinTryCntOut)
                        }
                        emvManager.setPinResponse(response)
                    }

                    override fun onCancel() {
                        pinFlowEndedByApp = true
                        emvManager.stopTransaction()
                        updateResult(-1, "PIN canceled")
                    }
                })
                show()
            }
        }
    }

    override fun onRequestOnlineProcess(info: Bundle) {
        Log.d(TAG, "onRequestOnlineProcess info=${Gson().toJson(info)}")
        reportProgress("Authorizing online...")
        transData.transData = info.getByteArray(POIEmvCoreManager.EmvOnlineConstraints.EMV_DATA)
        Log.d(TAG, "CVM=${extractCvm(transData.transData)}")

        val ret = emvManager.setOnlineResponse(processOnlineResult("8A023030"))
        if (ret != 0) {
            updateResult(-1, "setOnlineResponse failed: $ret")
        }
    }

    override fun onTransactionResult(resultCode: Int, info: Bundle) {
        Log.d(TAG, "onTransactionResult result=$resultCode info=${Gson().toJson(info)}")
        currentPinDialog?.dismiss()
        currentPinDialog = null
        runCatching {
            FinancialEngine.cardReaderManager?.powerOff(ConstantCardReader.CardType.MAGNETIC)
        }.onFailure {
            Log.w(TAG, "powerOff(MAGNETIC) failed", it)
        }

        when (resultCode) {
            PosEmvErrorCode.EMV_CANCEL -> {
                if (pinFlowEndedByApp) return
                updateResult(-1, "Transaction canceled at stage: $lastStage (code=$resultCode).")
                return
            }
            PosEmvErrorCode.EMV_TIMEOUT -> {
                updateResult(-1, "Transaction timeout at stage: $lastStage (code=$resultCode).")
                return
            }
            PosEmvErrorCode.EMV_MULTI_CONTACTLESS -> {
                updateResult(-1, "Multiple cards detected.")
                return
            }
            PosEmvErrorCode.EMV_FALLBACK -> {
                updateResult(-1, "Fallback is not allowed in this demo.")
                return
            }
            PosEmvErrorCode.EMV_OTHER_ICC_INTERFACE -> {
                reportProgress("Please insert card.")
                return
            }
            PosEmvErrorCode.EMV_SEE_PHONE,
            PosEmvErrorCode.APPLE_VAS_WAITING_INTERVENTION,
            PosEmvErrorCode.APPLE_VAS_WAITING_ACTIVATION -> {
                reportProgress("Please see phone.")
                return
            }
        }

        transData.transResult = resultCode
        transData.transData = info.getByteArray(POIEmvCoreManager.EmvResultConstraints.EMV_DATA)

        when (resultCode) {
            PosEmvErrorCode.EMV_APPROVED,
            PosEmvErrorCode.EMV_APPROVED_ONLINE,
            PosEmvErrorCode.EMV_FORCE_APPROVED,
            PosEmvErrorCode.EMV_DELAYED_APPROVED,
            PosEmvErrorCode.APPLE_VAS_APPROVED -> {
                updateResult(0, buildApprovedMessage(resultCode))
            }
            else -> updateResult(-1, "Transaction failed: $resultCode")
        }
    }

    private fun captureMagstripeInfo(info: Bundle): String {
        val track1 = readTrack(info, POIEmvCoreManager.EmvCardInfoConstraints.TRACK1)
        val track2 = readTrack(info, POIEmvCoreManager.EmvCardInfoConstraints.TRACK2)
        val track3 = readTrack(info, POIEmvCoreManager.EmvCardInfoConstraints.TRACK3)
        val pan = extractPan(track2, track1)

        transData.cardType = POIEmvCoreManager.DEVICE_MAGSTRIPE
        transData.cardNumber = pan?.let(::maskPan)

        Log.d(
            TAG,
            "Magstripe tracks received: " +
                "track1=${trackDebug(track1)}, " +
                "track2=${trackDebug(track2)}, " +
                "track3=${trackDebug(track3)}"
        )

        return transData.cardNumber?.let { "Magstripe card read: $it" }
            ?: "Processing magstripe card..."
    }

    private fun hasMagstripeTrack(info: Bundle): Boolean {
        return info.containsKey(POIEmvCoreManager.EmvCardInfoConstraints.TRACK1) ||
            info.containsKey(POIEmvCoreManager.EmvCardInfoConstraints.TRACK2) ||
            info.containsKey(POIEmvCoreManager.EmvCardInfoConstraints.TRACK3)
    }

    private fun readTrack(info: Bundle, key: String): String? {
        val bytes = info.getByteArray(key) ?: return null
        if (bytes.isEmpty()) return null

        val text = String(bytes, StandardCharsets.US_ASCII).trim { it <= ' ' || it == '\u0000' }
        val printableCount = text.count { it.code in 32..126 }
        return if (text.isNotEmpty() && printableCount * 10 >= text.length * 8) {
            text
        } else {
            ByteUtils.bytesToHexString(bytes)
        }
    }

    private fun extractPan(vararg tracks: String?): String? {
        tracks.filterNotNull().forEach { track ->
            listOf(
                Regex("""%?B?(\d{12,19})\^"""),
                Regex(""";?(\d{12,19})[D=]"""),
                Regex("""(\d{12,19})[D=]"""),
                Regex("""(\d{12,19})""")
            ).forEach { regex ->
                val pan = regex.find(track)?.groupValues?.getOrNull(1)
                if (!pan.isNullOrBlank()) return pan
            }
        }
        return null
    }

    private fun maskPan(pan: String): String {
        if (pan.length <= 10) return "****${pan.takeLast(4)}"
        return "${pan.take(6)}******${pan.takeLast(4)}"
    }

    private fun trackDebug(track: String?): String {
        if (track.isNullOrBlank()) return "none"
        return "len=${track.length}, pan=${extractPan(track)?.let(::maskPan) ?: "unknown"}"
    }

    private fun buildApprovedMessage(resultCode: Int): String {
        return buildString {
            append("Amount: ${transData.transAmount}")
            append("\nResult: $resultCode")
            if (transData.cardType == POIEmvCoreManager.DEVICE_MAGSTRIPE) {
                append("\nCard: Magstripe")
            }
            transData.cardNumber?.let { append("\nPAN: $it") }
            append("\nCVM: ${extractCvm(transData.transData)}")
        }
    }

    private fun logApplicationList(appList: List<String>) {
        appList.forEach { raw ->
            runCatching {
                val tlvs = BerTlvParser().parse(ByteUtils.hexStringToBytes(raw))
                val aid: BerTlv? = tlvs.find(BerTag("4F"))
                val label: BerTlv? = tlvs.find(BerTag("50"))
                Log.d(TAG, "AID=${aid?.hexValue.orEmpty()} label=${label?.hexValue.orEmpty()}")
            }.onFailure {
                Log.w(TAG, "Failed to parse app TLV: $raw", it)
            }
        }
    }

    private fun reportProgress(message: String) {
        lastStage = message
        updateProgress(message)
    }

    private companion object {
        const val TAG = "PaymentDemo.Emv"
    }
}
