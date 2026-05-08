package com.kozen.support.x.utils

import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import com.kozen.financial.constant.ConstantCardReader
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager.EmvTerminalConstraints
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager.EmvTransDataConstraints
import com.kozen.financial.constant.ConstantEmv.PosEmvErrorCode
import com.kozen.financial.emv.IEmvManager
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.util.emv.tlv.BerTag
import com.kozen.financial.util.emv.tlv.BerTlvBuilder
import com.kozen.financial.util.emv.tlv.BerTlvParser
import com.kozen.support.x.exception.TransactionException
import com.kozen.support.x.exception.TransactionExceptionEnum
import com.kozen.support.x.model.TransactionData
import com.kozen.support.x.ui.PaymentActivity

private const val TAG = "PaymentDemo.Transaction"
private const val TRANS_TIMEOUT_MS = 60_000

fun startTransaction(
    amountMinorUnits: Long,
    activity: PaymentActivity,
    updateProgress: (msg: String) -> Unit,
    updateResult: (code: Int, msg: String) -> Unit
) {
    val emvManager = FinancialEngine.emvManager
        ?: throw TransactionException(TransactionExceptionEnum.EMV_MANAGER_NOT_FOUND)

    prepare(emvManager)
    armMagstripeReader()

    val transactionData = TransactionData().apply {
        transType = POIEmvCoreManager.EMV_GOODS
        transAmount = amountMinorUnits
        transAmountOther = 0L
        transResult = PosEmvErrorCode.EMV_OTHER_ERROR
    }

    val ret = emvManager.startTransaction(
        buildTransactionBundle(transactionData),
        EmvCallbackAdapter(transactionData, emvManager, activity, updateProgress, updateResult)
    )
    if (ret != 0) {
        updateResult(-1, "startTransaction failed: $ret")
    }
}

private fun prepare(emvManager: IEmvManager) {
    setTerminal(emvManager)
    setAid(emvManager)
    setCapk(emvManager)
}

private fun armMagstripeReader() {
    val cardReaderManager = FinancialEngine.cardReaderManager
    if (cardReaderManager == null) {
        Log.w(TAG, "cardReaderManager is null; magstripe reader may not be armed")
        return
    }

    val ret = cardReaderManager.powerOn(ConstantCardReader.CardType.MAGNETIC)
    Log.d(TAG, "powerOn(MAGNETIC) returned $ret")
}

private fun setTerminal(emvManager: IEmvManager) {
    val terminal = Bundle().apply {
        putString(EmvTerminalConstraints.TERMINAL_ID, "TEST1234")
        putString(EmvTerminalConstraints.MERCHANT_NAME, "Kozen Demo")
        putString(EmvTerminalConstraints.MERCHANT_CATEGORY_CODE, "5999")
        putString(EmvTerminalConstraints.TERMINAL_COUNTRY_CODE, "0156")
    }

    val ret = emvManager.setTerminal(EmvTerminalConstraints.TYPE_TERMINAL, terminal)
    if (ret != 0) {
        Log.w(TAG, "setTerminal returned $ret")
    }
}

private fun setAid(emvManager: IEmvManager) {
    val aidList = AidUtil.createAidList()
    val ret = emvManager.setAidList(aidList)
    if (ret != 0) {
        Log.w(TAG, "setAidList returned $ret; falling back to setAid one by one")
        aidList.forEach { aid ->
            val code = emvManager.setAid(aid)
            if (code != 0) Log.w(TAG, "setAid failed: $code")
        }
    }
}

private fun setCapk(emvManager: IEmvManager) {
    val capkList = CapkUtil.createCapkList()
    val ret = emvManager.setCapkList(capkList)
    if (ret != 0) {
        Log.w(TAG, "setCapkList returned $ret; falling back to setCapk one by one")
        capkList.forEach { capk ->
            val code = emvManager.setCapk(capk)
            if (code != 0) Log.w(TAG, "setCapk failed: $code")
        }
    }
}

fun buildTransactionBundle(transactionData: TransactionData): Bundle {
    val mode = POIEmvCoreManager.DEVICE_CONTACT or
        POIEmvCoreManager.DEVICE_CONTACTLESS or
        POIEmvCoreManager.DEVICE_MAGSTRIPE or
        POIEmvCoreManager.DEVICE_VICC

    return Bundle().apply {
        putBoolean(EmvTransDataConstraints.ENCRYPT_TRACK_USE_BCD, true)
        putBoolean(EmvTransDataConstraints.DOUBLE_BCD, true)
        putInt(EmvTransDataConstraints.TRANS_TYPE, transactionData.transType)
        putLong(EmvTransDataConstraints.TRANS_AMOUNT, transactionData.transAmount)
        putLong(EmvTransDataConstraints.TRANS_AMOUNT_OTHER, transactionData.transAmountOther)
        putInt(EmvTransDataConstraints.TRANS_MODE, mode)
        putBoolean(EmvTransDataConstraints.APPLE_VAS, false)
        putInt(EmvTransDataConstraints.TRANS_TIMEOUT, TRANS_TIMEOUT_MS)
        putBoolean(EmvTransDataConstraints.USE_USA_VISA, false)
        putBoolean(EmvTransDataConstraints.USE_LOG, true)
        putBoolean(EmvTransDataConstraints.USE_FILTER, true)
        putBoolean(EmvTransDataConstraints.USE_MAGSTRIPE_FILTER, true)
        putBoolean(EmvTransDataConstraints.USE_SPECIAL_AID_SELECTION, true)
        putBoolean(EmvTransDataConstraints.USE_SELECT_AFTER_FILTER, true)
        putBoolean(EmvTransDataConstraints.USE_CARD_READ_SUCCESS, true)
        putBoolean(EmvTransDataConstraints.USE_GPO_BEFORE_FILTER, false)
        putBoolean(EmvTransDataConstraints.TRANS_FALLBACK, true)
        putBoolean(EmvTransDataConstraints.SPECIAL_CONTACT, false)
        putBoolean(EmvTransDataConstraints.SPECIAL_MAGSTRIPE, false)
        putBoolean(EmvTransDataConstraints.USE_MCCS, false)
        Log.d(TAG, "Transaction bundle: ${Gson().toJson(this)}")
    }
}

fun processOnlineResult(data: String): Bundle {
    val bundle = Bundle()
    val tlvBuilder = BerTlvBuilder()
    var authRespCode: String? = null
    var authData: String? = null
    var script: String? = null
    val tlvs = BerTlvParser().parse(ByteUtils.hexStringToBytes(data)).list

    for (tlv in tlvs) {
        when (tlv.tag.berTagHex) {
            "8A" -> authRespCode = tlv.hexValue
            "91" -> authData = tlv.hexValue
            "71", "72" -> tlvBuilder.addBerTlv(tlv)
        }
    }

    if (tlvBuilder.build() != 0) {
        script = ByteUtils.bytesToHexString(tlvBuilder.buildArray())
    }

    when (authRespCode) {
        "3030" -> {
            bundle.putInt(
                POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
                POIEmvCoreManager.EmvOnlineConstraints.EMV_ONLINE_APPROVE
            )
            bundle.putByteArray(
                POIEmvCoreManager.EmvOnlineConstraints.OUT_SPECIAL_AUTH_RESP_CODE,
                ByteUtils.hexStringToBytes("3030")
            )
        }
        "3031" -> bundle.putInt(
            POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
            POIEmvCoreManager.EmvOnlineConstraints.EMV_ONLINE_REFER_TO_CARD_ISSUER
        )
        "3032" -> bundle.putInt(
            POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
            POIEmvCoreManager.EmvOnlineConstraints.EMV_ONLINE_DENIAL
        )
        else -> bundle.putInt(
            POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_RESP_CODE,
            POIEmvCoreManager.EmvOnlineConstraints.EMV_ONLINE_FAIL
        )
    }

    authData?.let {
        bundle.putByteArray(POIEmvCoreManager.EmvOnlineConstraints.OUT_AUTH_DATA, ByteUtils.hexStringToBytes(it))
    }
    script?.let {
        bundle.putByteArray(POIEmvCoreManager.EmvOnlineConstraints.OUT_ISSUER_SCRIPT, ByteUtils.hexStringToBytes(it))
    }

    return bundle
}

fun extractCvm(data: ByteArray?): String {
    if (data == null) return "unknown"
    val outcome = BerTlvParser().parse(data).find(BerTag("DC"))?.bytesValue ?: return "unknown"
    if (outcome.size < 3) return "unknown"
    val value = outcome[2].toInt()
    return when {
        value == 0 -> "no CVM"
        value and 0x80 == 0x80 -> "signature"
        value and 0x40 == 0x40 -> "online PIN"
        value and 0x20 == 0x20 -> "enciphered PIN"
        value and 0x10 == 0x10 -> "plaintext PIN"
        value and 0x08 == 0x08 -> "CDCVM"
        else -> "unknown"
    }
}
