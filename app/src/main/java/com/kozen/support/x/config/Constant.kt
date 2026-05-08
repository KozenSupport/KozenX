package com.kozen.support.x.config

import com.kozen.financial.constant.ConstantSecurity

object DeviceConfig {
    const val isHardwareKeyboard: Boolean = false
    const val isHardwarePrinter: Boolean = false

    var isPinpadKeyOrderly: Boolean = false

    @JvmStatic
    var encryptionMechanism = ConstantSecurity.ENCRYPTION_MECHANISM_MK_SK

    @JvmStatic
    var encryptionAlgorithm = ConstantSecurity.ENCRYPTION_ALGORITHM_TDES

    @JvmStatic
    var pinKeyIndex = KeyIndexConstant.TPK_TDES_DATA_INDEX

    @JvmStatic
    var pinFormatMode = ConstantSecurity.PINBLOCK_TPK_FMT_ISO9564_0

    @JvmStatic
    var dukptAesKeyBits = DukptAesKeyBit.BIT_128

    @JvmStatic
    var pinByPass = false
}

object KeyIndexConstant {
    const val TLK_INDEX = 1
    const val TLK_NONE = 0

    const val RSA_PUBLIC_KEY_INDEX = 3
    const val RSA_PRIVATE_KEY_INDEX = 4
    const val RSA_KEY_SIZE = 1024

    const val DUKPT_TDES_TIK_INDEX = 5
    const val DUKPT_AES_TIK_128_INDEX = 6
    const val DUKPT_AES_TIK_192_INDEX = 7
    const val DUKPT_AES_TIK_256_INDEX = 8

    const val TMK_TDES_DATA_INDEX = 11
    const val TPK_TDES_DATA_INDEX = 12
    const val TAK_TDES_DATA_INDEX = 13
    const val TDK_TDES_DATA_INDEX = 14
    const val TEK_TDES_DATA_INDEX = 15
    const val TTK_TDES_DATA_INDEX = 16

    const val TMK_AES_DATA_INDEX = 21
    const val TPK_AES_DATA_INDEX = 22
    const val TAK_AES_DATA_INDEX = 23
    const val TDK_AES_DATA_INDEX = 24
    const val TEK_AES_DATA_INDEX = 25
    const val TTK_AES_DATA_INDEX = 26

    const val TMK_SM4_DATA_INDEX = 31
    const val TPK_SM4_DATA_INDEX = 32
    const val TAK_SM4_DATA_INDEX = 33
    const val TDK_SM4_DATA_INDEX = 34
    const val TEK_SM4_DATA_INDEX = 35
    const val TTK_SM4_DATA_INDEX = 36
}
