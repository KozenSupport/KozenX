package com.kozen.support.x.utils

import android.util.Log
import com.kozen.financial.constant.ConstantSecurity
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.security.ISecurityManager
import com.kozen.financial.util.emv.tlv.HexUtil
import com.kozen.support.x.config.KeyIndexConstant
import com.kozen.support.x.exception.TransactionException
import com.kozen.support.x.exception.TransactionExceptionEnum

private const val KEY_TAG = "PaymentDemo.Keys"

private data class MkskKeySet(
    val label: String,
    val tmkIndex: Int,
    val tpkIndex: Int,
    val tdkIndex: Int,
    val takIndex: Int
)

fun writeKeys() {
    val securityManager = FinancialEngine.securityManager
        ?: throw TransactionException(TransactionExceptionEnum.SECURITY_MANAGER_NOT_FOUND)

    // Demo only: load known test keys into fixed slots. Do not erase all keys here;
    // the API explorer may run on real terminals that already contain other keys.
    writeKeyMKSK(securityManager)
}

private fun writeKeyMKSK(securityManager: ISecurityManager) {
    val tlkIndex = KeyIndexConstant.TLK_INDEX
    val keySets = listOf(
        MkskKeySet("legacy", tmkIndex = 2, tpkIndex = 3, tdkIndex = 4, takIndex = 5),
        MkskKeySet(
            "configured",
            tmkIndex = KeyIndexConstant.TMK_TDES_DATA_INDEX,
            tpkIndex = KeyIndexConstant.TPK_TDES_DATA_INDEX,
            tdkIndex = KeyIndexConstant.TDK_TDES_DATA_INDEX,
            takIndex = KeyIndexConstant.TAK_TDES_DATA_INDEX
        )
    )

    writeMksk(
        securityManager,
        sourceType = ConstantSecurity.PED_TLK,
        sourceIndex = 0,
        destType = ConstantSecurity.PED_TLK,
        destIndex = tlkIndex,
        key = "11111111111111112222222222222222",
        kcv = "D2B9",
        label = "TLK"
    )

    keySets.forEach { keySet ->
        writeMksk(
            securityManager,
            sourceType = ConstantSecurity.PED_TLK,
            sourceIndex = tlkIndex,
            destType = ConstantSecurity.PED_TMK,
            destIndex = keySet.tmkIndex,
            key = "CB4AB541CD5AD4FCCB4AB541CD5AD4FC",
            kcv = "D5D4",
            label = "${keySet.label} TMK"
        )
        writeMksk(
            securityManager,
            sourceType = ConstantSecurity.PED_TMK,
            sourceIndex = keySet.tmkIndex,
            destType = ConstantSecurity.PED_TPK,
            destIndex = keySet.tpkIndex,
            key = "BBD656EC790038A483D16B9E14A35C9A",
            kcv = "89C4",
            label = "${keySet.label} TPK"
        )
        writeMksk(
            securityManager,
            sourceType = ConstantSecurity.PED_TMK,
            sourceIndex = keySet.tmkIndex,
            destType = ConstantSecurity.PED_TDK,
            destIndex = keySet.tdkIndex,
            key = "6AC292FAA1315B4D858AB3A3D7D5933A",
            kcv = "25F5",
            label = "${keySet.label} TDK"
        )
        writeMksk(
            securityManager,
            sourceType = ConstantSecurity.PED_TMK,
            sourceIndex = keySet.tmkIndex,
            destType = ConstantSecurity.PED_TAK,
            destIndex = keySet.takIndex,
            key = "CF00BFDE6A5906DAAAEF50A2521C39A7",
            kcv = "99C2",
            label = "${keySet.label} TAK"
        )
    }
}

private fun writeMksk(
    securityManager: ISecurityManager,
    sourceType: Int,
    sourceIndex: Int,
    destType: Int,
    destIndex: Int,
    key: String,
    kcv: String,
    label: String
) {
    val ret = securityManager.writeKeyMKSK(
        sourceType,
        sourceIndex,
        destType,
        ConstantSecurity.ENCRYPTION_ALGORITHM_TDES,
        destIndex,
        HexUtil.parseHex(key),
        HexUtil.parseHex(kcv)
    )
    if (ret == 0) {
        Log.d(KEY_TAG, "writeKey $label success")
    } else {
        Log.w(KEY_TAG, "writeKey $label returned $ret")
    }
}
