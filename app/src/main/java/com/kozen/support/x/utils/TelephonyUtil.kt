package com.kozen.support.x.utils

import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.euicc.EuiccManager
import androidx.annotation.RequiresApi


/**
 * TelephonyUtil
 * Static utility class for SIM and mobile network tests.
 */
object TelephonyUtil {


    /**
     * Gets SIM slot count.
     * Test method: TelephonyManager.phoneCount
     */
    fun getSimSlotCount(context: Context): Int {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.phoneCount
    }


    /**
     * Gets ICCID of SIM card.
     * Test method: TelephonyManager.simSerialNumber
     */
    fun getIccid(context: Context): String? {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return tm.simSerialNumber
    }


    /**
     * Gets EID of eSIM.
     * Test method: TelephonyManager.eid (API 29+)
     */
    @RequiresApi(Build.VERSION_CODES.P) // API 28+
    fun getEid(context: Context): String {
        val euiccManager =
            context.getSystemService(Context.EUICC_SERVICE) as? EuiccManager
                ?: return "EuiccManager not available (device has no eSIM)"

        return try {
            euiccManager.eid ?: "EID not available (permission or restriction)"
        } catch (e: SecurityException) {
            "EID access denied: missing READ_PHONE_STATE permission"
        }
    }




}