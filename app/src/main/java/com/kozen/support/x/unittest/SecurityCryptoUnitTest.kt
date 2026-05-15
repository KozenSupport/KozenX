package com.kozen.support.x.unittest

import android.content.Context
import com.kozen.financial.constant.ConstantSecurity
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.security.ISecurityManager
import com.kozen.financial.util.emv.tlv.HexUtil
import com.kozen.support.x.R
import com.kozen.support.x.config.KeyIndexConstant
import com.kozen.support.x.utils.FinancialSdkErrorTranslator
import java.util.Locale

object SecurityCryptoUnitTest : UnitTestCase {
    private const val TLK = "11111111111111112222222222222222"
    private const val TLK_KCV = "D2B9"
    private const val TMK = "CB4AB541CD5AD4FCCB4AB541CD5AD4FC"
    private const val TMK_KCV = "D5D4"
    private const val TPK = "BBD656EC790038A483D16B9E14A35C9A"
    private const val TPK_KCV = "89C4"
    private const val TDK = "6AC292FAA1315B4D858AB3A3D7D5933A"
    private const val TDK_KCV = "25F5"
    private const val TAK = "CF00BFDE6A5906DAAAEF50A2521C39A7"
    private const val TAK_KCV = "99C2"

    private const val DUKPT_TIK = "6AC292FAA1315B4D858AB3A3D7D5933A"
    private const val DUKPT_TIK_KSN = "FFFF9876543210E00000"
    private const val DUKPT_TIK_KCV = "AF8C074A"

    private const val TEST_DATA = "00112233445566778899AABBCCDDEEFF"
    private const val MAC_DATA = "B02310D37A8A9D7952C1C1D5F8F73D61C1D0F8FB4958670D"

    override fun runningMessage(context: Context): String {
        return context.getString(R.string.unit_test_security_running)
    }

    override fun successMessage(context: Context): String {
        return context.getString(R.string.unit_test_security_success)
    }

    override fun start(context: Context, callback: UnitTestCallback) {
        FinancialUnitTestSupport.ensureFinancialSdkReady(context, callback) {
            Thread {
                try {
                    val securityManager = FinancialEngine.securityManager
                        ?: throw TestFailure(context.getString(R.string.unit_test_security_manager_missing))

                    writeMkskKeys(context, securityManager)
                    verifyMkskKcv(context, securityManager)
                    writeDukptKey(context, securityManager)
                    verifyKcv(
                        context,
                        securityManager,
                        ConstantSecurity.PED_TIK,
                        KeyIndexConstant.DUKPT_TDES_TIK_INDEX,
                        DUKPT_TIK_KCV
                    )
                    verifyMkskCrypto(context, securityManager)
                    verifyDukptCrypto(context, securityManager)
                    callback.onSuccess()
                } catch (e: TestFailure) {
                    callback.onFailure(e.message.orEmpty())
                } catch (e: Exception) {
                    callback.onFailure(
                        context.getString(R.string.unit_test_security_failed_exception, e.message.orEmpty())
                    )
                }
            }.start()
        }
    }

    private fun writeMkskKeys(context: Context, securityManager: ISecurityManager) {
        requireSuccess(
            context,
            "write TLK",
            securityManager.writeKeyMKSK(
                ConstantSecurity.PED_TLK,
                0,
                ConstantSecurity.PED_TLK,
                ConstantSecurity.ENCRYPTION_ALGORITHM_TDES,
                KeyIndexConstant.TLK_INDEX,
                hex(TLK),
                hex(TLK_KCV)
            )
        )
        writeMkskChild(context, securityManager, ConstantSecurity.PED_TMK, KeyIndexConstant.TMK_TDES_DATA_INDEX, TMK, TMK_KCV, "write TMK")
        writeMkskChild(context, securityManager, ConstantSecurity.PED_TPK, KeyIndexConstant.TPK_TDES_DATA_INDEX, TPK, TPK_KCV, "write TPK")
        writeMkskChild(context, securityManager, ConstantSecurity.PED_TDK, KeyIndexConstant.TDK_TDES_DATA_INDEX, TDK, TDK_KCV, "write TDK")
        writeMkskChild(context, securityManager, ConstantSecurity.PED_TAK, KeyIndexConstant.TAK_TDES_DATA_INDEX, TAK, TAK_KCV, "write TAK")
    }

    private fun writeMkskChild(
        context: Context,
        securityManager: ISecurityManager,
        keyType: Int,
        keyIndex: Int,
        key: String,
        kcv: String,
        operation: String
    ) {
        requireSuccess(
            context,
            operation,
            securityManager.writeKeyMKSK(
                if (keyType == ConstantSecurity.PED_TMK) ConstantSecurity.PED_TLK else ConstantSecurity.PED_TMK,
                if (keyType == ConstantSecurity.PED_TMK) KeyIndexConstant.TLK_INDEX else KeyIndexConstant.TMK_TDES_DATA_INDEX,
                keyType,
                ConstantSecurity.ENCRYPTION_ALGORITHM_TDES,
                keyIndex,
                hex(key),
                hex(kcv)
            )
        )
    }

    private fun verifyMkskKcv(context: Context, securityManager: ISecurityManager) {
        verifyKcv(context, securityManager, ConstantSecurity.PED_TLK, KeyIndexConstant.TLK_INDEX, TLK_KCV)
        verifyKcv(context, securityManager, ConstantSecurity.PED_TMK, KeyIndexConstant.TMK_TDES_DATA_INDEX, TMK_KCV)
        verifyKcv(context, securityManager, ConstantSecurity.PED_TPK, KeyIndexConstant.TPK_TDES_DATA_INDEX, TPK_KCV)
        verifyKcv(context, securityManager, ConstantSecurity.PED_TDK, KeyIndexConstant.TDK_TDES_DATA_INDEX, TDK_KCV)
        verifyKcv(context, securityManager, ConstantSecurity.PED_TAK, KeyIndexConstant.TAK_TDES_DATA_INDEX, TAK_KCV)
    }

    private fun writeDukptKey(context: Context, securityManager: ISecurityManager) {
        requireSuccess(
            context,
            "write DUKPT TIK",
            securityManager.writeKeyDukptDes(
                KeyIndexConstant.DUKPT_TDES_TIK_INDEX,
                KeyIndexConstant.TLK_NONE,
                hex(DUKPT_TIK),
                hex(DUKPT_TIK_KSN),
                ConstantSecurity.KCV_MODE_CHK_0,
                hex(DUKPT_TIK_KCV)
            )
        )
    }

    private fun verifyMkskCrypto(context: Context, securityManager: ISecurityManager) {
        val dataIn = hex(TEST_DATA)
        val encrypted = ByteArray(dataIn.size)
        requireSuccess(
            context,
            "MK/SK encrypt",
            securityManager.calcDes(
                KeyIndexConstant.TDK_TDES_DATA_INDEX,
                ConstantSecurity.PED_CALC_DES_MODE_ECB_ENC,
                dataIn,
                encrypted
            )
        )

        val decrypted = ByteArray(dataIn.size)
        requireSuccess(
            context,
            "MK/SK decrypt",
            securityManager.calcDes(
                KeyIndexConstant.TDK_TDES_DATA_INDEX,
                ConstantSecurity.PED_CALC_DES_MODE_ECB_DEC,
                encrypted,
                decrypted
            )
        )
        requireBytes("MK/SK decrypt verify", dataIn, decrypted)

        val mac = ByteArray(8)
        requireSuccess(
            context,
            "MK/SK MAC",
            securityManager.calcMac(
                KeyIndexConstant.TAK_TDES_DATA_INDEX,
                ConstantSecurity.MAC_ALGORITHM_ANSI_X9_19,
                ByteArray(8),
                hex(MAC_DATA),
                mac
            )
        )
        requireNonZero("MK/SK MAC verify", mac)
    }

    private fun verifyDukptCrypto(context: Context, securityManager: ISecurityManager) {
        val dataIn = hex(TEST_DATA)
        val encrypted = ByteArray(dataIn.size)
        val ksnOut = ByteArray(10)
        requireSuccess(
            context,
            "DUKPT encrypt",
            securityManager.calcDukptDes(
                KeyIndexConstant.DUKPT_TDES_TIK_INDEX,
                ConstantSecurity.DUKPT_KEY_SELECT_DATA_REQUEST,
                ConstantSecurity.OPERATION_DIRECTION_ENCRYPT,
                ConstantSecurity.OPERATION_MODE_ECB,
                ConstantSecurity.NOT_SELF_INCREASING,
                dataIn,
                ByteArray(8),
                encrypted,
                ksnOut
            )
        )

        val decrypted = ByteArray(dataIn.size)
        requireSuccess(
            context,
            "DUKPT decrypt",
            securityManager.calcDukptDes(
                KeyIndexConstant.DUKPT_TDES_TIK_INDEX,
                ConstantSecurity.DUKPT_KEY_SELECT_DATA_REQUEST,
                ConstantSecurity.OPERATION_DIRECTION_DECRYPT,
                ConstantSecurity.OPERATION_MODE_ECB,
                ConstantSecurity.NOT_SELF_INCREASING,
                encrypted,
                ByteArray(8),
                decrypted,
                ByteArray(10)
            )
        )
        requireBytes("DUKPT decrypt verify", dataIn, decrypted)

        val mac = ByteArray(8)
        requireSuccess(
            context,
            "DUKPT MAC",
            securityManager.calcMacDukptDes(
                KeyIndexConstant.DUKPT_TDES_TIK_INDEX,
                ConstantSecurity.OPERATION_MODE_ECB,
                ConstantSecurity.KSN_NOT_AUTO_INCREASING_BY_DUKPT_TDES_MAC_BOTH_KEY,
                ConstantSecurity.MAC_ALGORITHM_ANSI_X9_19,
                hex(MAC_DATA),
                ByteArray(8),
                mac,
                ByteArray(10)
            )
        )
        requireNonZero("DUKPT MAC verify", mac)
    }

    private fun verifyKcv(
        context: Context,
        securityManager: ISecurityManager,
        keyType: Int,
        keyIndex: Int,
        expectedPrefix: String
    ) {
        val kcvOut = ByteArray(8)
        requireSuccess(
            context,
            "get KCV type=$keyType index=$keyIndex",
            securityManager.getKCV(keyIndex, keyType, kcvOut)
        )
        val actual = HexUtil.toHexString(kcvOut).uppercase(Locale.US)
        if (!actual.startsWith(expectedPrefix.uppercase(Locale.US))) {
            throw TestFailure("KCV mismatch type=$keyType index=$keyIndex expected=$expectedPrefix actual=$actual")
        }
    }

    private fun requireSuccess(context: Context, operation: String, code: Int?) {
        if (code != FinancialUnitTestSupport.SUCCESS) {
            throw TestFailure(FinancialSdkErrorTranslator.formatFailure(context, operation, code))
        }
    }

    private fun requireBytes(operation: String, expected: ByteArray, actual: ByteArray) {
        if (!expected.contentEquals(actual)) {
            throw TestFailure(
                "$operation failed: expected=${HexUtil.toHexString(expected)} actual=${HexUtil.toHexString(actual)}"
            )
        }
    }

    private fun requireNonZero(operation: String, data: ByteArray) {
        if (data.all { it == 0.toByte() }) {
            throw TestFailure("$operation failed: empty output")
        }
    }

    private fun hex(value: String): ByteArray = HexUtil.parseHex(value)

    private class TestFailure(message: String) : Exception(message)
}
