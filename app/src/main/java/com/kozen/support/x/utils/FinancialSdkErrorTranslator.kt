package com.kozen.support.x.utils

import android.content.Context
import android.os.Build
import com.kozen.financial.constant.errorcode.CommonError
import com.kozen.financial.constant.errorcode.PinpadError
import com.kozen.financial.constant.errorcode.PrinterError
import com.kozen.financial.constant.errorcode.SecurityError
import java.util.Locale

object FinancialSdkErrorTranslator {
    fun formatFailure(context: Context, operation: String, code: Int?): String {
        return "$operation failed: ${describe(context, code)}"
    }

    fun describe(context: Context, code: Int?): String {
        if (code == null) {
            return if (useChinese(context)) "SDK 未返回错误码" else "SDK did not return an error code"
        }

        val knownError = FinancialSdkErrorCode.from(code)
        if (knownError != null) {
            val reason = if (useChinese(context)) knownError.zhReason else knownError.enReason
            return "${knownError.symbol}: $reason (${formatCode(code)})"
        }

        val unsignedHint = unsignedShortHint(code)
        val unknown = if (useChinese(context)) "未知 SDK 错误" else "Unknown SDK error"
        return if (unsignedHint == null) {
            "$unknown (${formatCode(code)})"
        } else {
            "$unknown (${formatCode(code)}, signed16=$unsignedHint)"
        }
    }

    private fun useChinese(context: Context): Boolean {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
        return locale.language.equals(Locale.CHINESE.language, ignoreCase = true)
    }

    private fun formatCode(code: Int): String {
        val hex = if (code in 0..0xFFFF) {
            String.format(Locale.US, "0x%04X", code)
        } else {
            String.format(Locale.US, "0x%08X", code)
        }
        return "code=$code/$hex"
    }

    private fun unsignedShortHint(code: Int): Int? {
        return if (code in 32768..0xFFFF) code.toShort().toInt() else null
    }
}

enum class FinancialSdkErrorCode(
    val code: Int,
    val symbol: String,
    val zhReason: String,
    val enReason: String
) {
    SDK_INIT_FAILED(
        -1,
        "SDK_INIT_FAILED",
        "SDK 初始化或服务连接失败，请重新初始化后再调用接口",
        "SDK initialization or service connection failed; initialize the SDK again before calling APIs"
    ),
    FINANCIAL_VERSION_NOT_MATCH(
        CommonError.FINANCIAL_VERSION_NOT_MATCH,
        "FINANCIAL_VERSION_NOT_MATCH",
        "Financial Service 与客户端 SDK 版本不匹配",
        "Financial Service and client SDK versions do not match"
    ),
    FINANCIAL_SERVICE_DISCONNECT(
        CommonError.FINANCIAL_SERVICE_DISCONNECT,
        "FINANCIAL_SERVICE_DISCONNECT",
        "金融服务未连接，请先初始化 Financial SDK",
        "Financial service is not connected; initialize the Financial SDK first"
    ),
    FINANCIAL_PARAMETERS_INVALID(
        CommonError.FINANCIAL_PARAMETERS_INVALID,
        "FINANCIAL_PARAMETERS_INVALID",
        "接口参数非法或格式不正确",
        "Illegal parameters or invalid parameter format"
    ),
    FEATURE_NOPERMISSION(
        CommonError.FEATURE_NOPERMISSION,
        "FEATURE_NOPERMISSION",
        "当前应用没有调用该功能所需权限",
        "The app does not have permission for this feature"
    ),
    FEATURE_UNSUPPORTED(
        CommonError.FEATURE_UNSUPPORTED,
        "FEATURE_UNSUPPORTED",
        "当前设备或服务版本不支持该功能",
        "This feature is not supported by the current device or service version"
    ),

    PRINTER_ERROR_INIT(
        PrinterError.PRINTER_ERROR_INIT,
        "PRINTER_ERROR_INIT",
        "打印模块未初始化或初始化失败",
        "Printer module is not initialized or initialization failed"
    ),
    PRINTER_ERROR_NO_PRINTER(
        PrinterError.PRINTER_ERROR_NO_PRINTER,
        "PRINTER_ERROR_NO_PRINTER",
        "未找到打印设备",
        "No printer device found"
    ),
    PRINTER_ERROR_NOT_OPENED(
        PrinterError.PRINTER_ERROR_NOT_OPENED,
        "PRINTER_ERROR_NOT_OPENED",
        "打印机未打开，请先调用 open",
        "Printer is not opened; call open first"
    ),
    PRINTER_ERROR_PRINT(
        PrinterError.PRINTER_ERROR_PRINT,
        "PRINTER_ERROR_PRINT",
        "打印失败，可尝试关闭并重新打开打印机",
        "Printing failed; close and reopen the printer before retrying"
    ),
    PRINTER_ERROR_OVERHEAT(
        PrinterError.PRINTER_ERROR_OVERHEAT,
        "PRINTER_ERROR_OVERHEAT",
        "打印机过热，请等待降温后重试",
        "Printer is overheated; wait for it to cool down before retrying"
    ),
    PRINTER_ERROR_NO_PAPER(
        PrinterError.PRINTER_ERROR_NO_PAPER,
        "PRINTER_ERROR_NO_PAPER",
        "打印机缺纸",
        "Printer is out of paper"
    ),
    PRINTER_ERROR_LOW_POWER(
        PrinterError.PRINTER_ERROR_LOW_POWER,
        "PRINTER_ERROR_LOW_POWER",
        "设备电量低于打印阈值",
        "Device power is below the printer threshold"
    ),
    PRINTER_ERROR_NO_CONTENT(
        PrinterError.PRINTER_ERROR_NO_CONTENT,
        "PRINTER_ERROR_NO_CONTENT",
        "未添加打印内容",
        "No print content was added"
    ),
    PRINTER_ERROR_OTHER(
        PrinterError.PRINTER_ERROR_OTHER,
        "PRINTER_ERROR_OTHER",
        "打印模块未知错误",
        "Unknown printer module error"
    ),
    PRINTER_ERROR_QUEUE_OVER_FLOW(
        PrinterError.PRINTER_ERROR_QUEUE_OVER_FLOW,
        "PRINTER_ERROR_QUEUE_OVER_FLOW",
        "打印任务缓冲区溢出",
        "Print job buffer overflow"
    ),
    PRINTER_ERROR_SCREEN_OFF(
        PrinterError.PRINTER_ERROR_SCREEN_OFF,
        "PRINTER_ERROR_SCREEN_OFF",
        "休眠或息屏状态下禁止打印",
        "Printing is disabled while the screen is off or sleeping"
    ),
    PRINTER_ERROR_NOT_SUPPORT(
        PrinterError.PRINTER_ERROR_NOT_SUPPORT,
        "PRINTER_ERROR_NOT_SUPPORT",
        "打印机不支持该能力",
        "Printer does not support this capability"
    ),
    PRINTER_ERROR_PRINTING(
        PrinterError.PRINTER_ERROR_PRINTING,
        "PRINTER_ERROR_PRINTING",
        "打印机正在打印，请稍后重试",
        "Printer is already printing; try again later"
    ),

    PINPAD_OTHER_ERROR(
        PinpadError.PINPAD_OTHER_ERROR,
        "PINPAD_OTHER_ERROR",
        "密码键盘发生其他错误",
        "PIN pad returned another error"
    ),
    PINPAD_START_ERROR(
        PinpadError.PINPAD_START_ERROR,
        "PINPAD_START_ERROR",
        "启动密码键盘失败",
        "Failed to start PIN pad"
    ),
    PINPAD_CANCEL_ERROR(
        PinpadError.PINPAD_CANCEL_ERROR,
        "PINPAD_CANCEL_ERROR",
        "PIN 输入被取消",
        "PIN entry was canceled"
    ),
    PINPAD_SCREEN_ORIENTATION_CHANGED(
        PinpadError.PINPAD_SCREEN_ORIENTATION_CHANGED,
        "PINPAD_SCREEN_ORIENTATION_CHANGED",
        "PIN 输入过程中屏幕方向发生变化",
        "Screen orientation changed during PIN entry"
    ),
    PIN_KEY_COORDINATE_CALCULATION_ERROR(
        PinpadError.PIN_KEY_COORDINATE_CALCULATION_ERROR,
        "PIN_KEY_COORDINATE_CALCULATION_ERROR",
        "密码键盘按键坐标计算失败",
        "Failed to calculate PIN key coordinates"
    ),

    SECURITY_ERROR_INIT(
        SecurityError.SECURITY_ERROR_INIT,
        "SECURITY_ERROR_INIT",
        "安全算法模块未初始化",
        "Security algorithm module is not initialized"
    ),
    SECURITY_OTHER_ERROR(
        SecurityError.SECURITY_OTHER_ERROR,
        "SECURITY_OTHER_ERROR",
        "安全模块内部错误或系统接口调用异常",
        "Security module internal error or system interface failure"
    ),
    SECURITY_PARAMETERS_INVALID(
        SecurityError.SECURITY_PARAMETERS_INVALID,
        "SECURITY_PARAMETERS_INVALID",
        "安全接口参数为空或格式非法",
        "Security API parameters are empty or invalid"
    ),
    SECURITY_KEY_TYPE_OUT_OF_RANGE(
        SecurityError.SECURITY_KEY_TYPE_OUT_OF_RANGE,
        "SECURITY_KEY_TYPE_OUT_OF_RANGE",
        "密钥类型超出支持范围",
        "Key type is out of range"
    ),
    SECURITY_KEY_INDEX_OUT_OF_RANGE(
        SecurityError.SECURITY_KEY_INDEX_OUT_OF_RANGE,
        "SECURITY_KEY_INDEX_OUT_OF_RANGE",
        "密钥索引超出支持范围",
        "Key index is out of range"
    ),
    SECURITY_DATA_INDEX_OUT_OF_RANGE(
        SecurityError.SECURITY_DATA_INDEX_OUT_OF_RANGE,
        "SECURITY_DATA_INDEX_OUT_OF_RANGE",
        "数据索引超出支持范围",
        "Data index is out of range"
    ),
    SECURITY_TLK_INDEX_OUT_OF_RANGE(
        SecurityError.SECURITY_TLK_INDEX_OUT_OF_RANGE,
        "SECURITY_TLK_INDEX_OUT_OF_RANGE",
        "TLK 索引超出支持范围",
        "TLK index is out of range"
    ),
    SECURITY_KEY_IN_EMPTY_ERROR(
        SecurityError.SECURITY_KEY_IN_EMPTY_ERROR,
        "SECURITY_KEY_IN_EMPTY_ERROR",
        "写入的密钥数据为空",
        "Input key data is empty"
    ),
    SECURITY_DATA_IN_EMPTY_ERROR(
        SecurityError.SECURITY_DATA_IN_EMPTY_ERROR,
        "SECURITY_DATA_IN_EMPTY_ERROR",
        "输入数据为空",
        "Input data is empty"
    ),
    SECURITY_DATA_OUT_NULL_ERROR(
        SecurityError.SECURITY_DATA_OUT_NULL_ERROR,
        "SECURITY_DATA_OUT_NULL_ERROR",
        "输出缓冲区为空",
        "Output buffer is null"
    ),
    SECURITY_DATA_OUT_LENGTH_ERROR(
        SecurityError.SECURITY_DATA_OUT_LENGTH_ERROR,
        "SECURITY_DATA_OUT_LENGTH_ERROR",
        "输出缓冲区长度不足或不合法",
        "Output buffer length is too small or invalid"
    ),
    SECURITY_KCV_MODE_ERROR(
        SecurityError.SECURITY_KCV_MODE_ERROR,
        "SECURITY_KCV_MODE_ERROR",
        "KCV 校验模式非法，支持 0/1/2/3",
        "Illegal KCV verification mode; supported values are 0/1/2/3"
    ),
    SECURITY_KCV_ERROR(
        SecurityError.SECURITY_KCV_ERROR,
        "SECURITY_KCV_ERROR",
        "KCV 校验失败，请检查密钥值和 KCV 是否匹配",
        "KCV verification failed; check that the key value matches the KCV"
    ),
    SECURITY_KCV_VALUE_OUT_OF_RANGE(
        SecurityError.SECURITY_KCV_VALUE_OUT_OF_RANGE,
        "SECURITY_KCV_VALUE_OUT_OF_RANGE",
        "KCV 长度或取值不符合安全模块要求",
        "KCV length or value is not accepted by the security module"
    ),
    SECURITY_MKSK_KEY_LENGTH_ERROR(
        SecurityError.SECURITY_MKSK_KEY_LENGTH_ERROR,
        "SECURITY_MKSK_KEY_LENGTH_ERROR",
        "MK/SK 密钥长度不合法，支持 8/16/24/32 字节",
        "Invalid MK/SK key length; supported lengths are 8/16/24/32 bytes"
    ),
    SECURITY_MKSK_SRC_KEY_TYPE_ERROR(
        SecurityError.SECURITY_MKSK_SRC_KEY_TYPE_ERROR,
        "SECURITY_MKSK_SRC_KEY_TYPE_ERROR",
        "MK/SK 源密钥类型不合法",
        "Invalid MK/SK source key type"
    ),
    SECURITY_MKSK_SRC_KEY_INDEX_ERROR(
        SecurityError.SECURITY_MKSK_SRC_KEY_INDEX_ERROR,
        "SECURITY_MKSK_SRC_KEY_INDEX_ERROR",
        "MK/SK 源密钥索引不合法",
        "Invalid MK/SK source key index"
    ),
    SECURITY_MKSK_KEY_TYPE_ERROR(
        SecurityError.SECURITY_MKSK_KEY_TYPE_ERROR,
        "SECURITY_MKSK_KEY_TYPE_ERROR",
        "MK/SK 目标密钥类型不合法",
        "Invalid MK/SK target key type"
    ),
    SECURITY_MKSK_KEY_INDEX_ERROR(
        SecurityError.SECURITY_MKSK_KEY_INDEX_ERROR,
        "SECURITY_MKSK_KEY_INDEX_ERROR",
        "MK/SK 目标密钥索引不合法",
        "Invalid MK/SK target key index"
    ),
    SECURITY_MKSK_ENCRYPTION_ALGORITHM_ERROR(
        SecurityError.SECURITY_MKSK_ENCRYPTION_ALGORITHM_ERROR,
        "SECURITY_MKSK_ENCRYPTION_ALGORITHM_ERROR",
        "MK/SK 加密算法不合法",
        "Invalid MK/SK encryption algorithm"
    ),
    SECURITY_MKSK_CALC_MODE_ERROR(
        SecurityError.SECURITY_MKSK_CALC_MODE_ERROR,
        "SECURITY_MKSK_CALC_MODE_ERROR",
        "MK/SK 加解密模式不合法",
        "Invalid MK/SK calculation mode"
    ),
    SECURITY_MKSK_MAC_MODE_ERROR(
        SecurityError.SECURITY_MKSK_MAC_MODE_ERROR,
        "SECURITY_MKSK_MAC_MODE_ERROR",
        "MK/SK MAC 算法或模式不合法",
        "Invalid MK/SK MAC algorithm or mode"
    ),
    SECURITY_DUKPT_TIK_INDEX_ERROR(
        SecurityError.SECURITY_DUKPT_TIK_INDEX_ERROR,
        "SECURITY_DUKPT_TIK_INDEX_ERROR",
        "DUKPT TIK 索引不合法，支持 1~10",
        "Invalid DUKPT TIK index; supported range is 1-10"
    ),
    SECURITY_DUKPT_SRC_KEY_INDEX_ERROR(
        SecurityError.SECURITY_DUKPT_SRC_KEY_INDEX_ERROR,
        "SECURITY_DUKPT_SRC_KEY_INDEX_ERROR",
        "DUKPT 源密钥索引不合法",
        "Invalid DUKPT source key index"
    ),
    SECURITY_DUKPT_KEY_USAGE_ERROR(
        SecurityError.SECURITY_DUKPT_KEY_USAGE_ERROR,
        "SECURITY_DUKPT_KEY_USAGE_ERROR",
        "DUKPT 密钥用途不合法",
        "Invalid DUKPT key usage"
    ),
    SECURITY_DUKPT_KEY_ALG_TYPE_ERROR(
        SecurityError.SECURITY_DUKPT_KEY_ALG_TYPE_ERROR,
        "SECURITY_DUKPT_KEY_ALG_TYPE_ERROR",
        "DUKPT 密钥算法类型不合法",
        "Invalid DUKPT key algorithm type"
    ),
    SECURITY_DUKPT_KEY_TYPE_ERROR(
        SecurityError.SECURITY_DUKPT_KEY_TYPE_ERROR,
        "SECURITY_DUKPT_KEY_TYPE_ERROR",
        "DUKPT 密钥类型不合法",
        "Invalid DUKPT key type"
    ),
    SECURITY_DUKPT_OPERATION_MODE_ERROR(
        SecurityError.SECURITY_DUKPT_OPERATION_MODE_ERROR,
        "SECURITY_DUKPT_OPERATION_MODE_ERROR",
        "DUKPT 运算模式不合法",
        "Invalid DUKPT operation mode"
    ),
    SECURITY_DUKPT_OPERATION_DIRECTION_ERROR(
        SecurityError.SECURITY_DUKPT_OPERATION_DIRECTION_ERROR,
        "SECURITY_DUKPT_OPERATION_DIRECTION_ERROR",
        "DUKPT 运算方向不合法",
        "Invalid DUKPT operation direction"
    ),
    SECURITY_DUKPT_KSN_MODE_ERROR(
        SecurityError.SECURITY_DUKPT_KSN_MODE_ERROR,
        "SECURITY_DUKPT_KSN_MODE_ERROR",
        "DUKPT KSN 自增模式不合法",
        "Invalid DUKPT KSN mode"
    ),
    SECURITY_DUKPT_KEY_LENGTH_ERROR(
        SecurityError.SECURITY_DUKPT_KEY_LENGTH_ERROR,
        "SECURITY_DUKPT_KEY_LENGTH_ERROR",
        "DUKPT TIK 长度不合法，TDES TIK 通常支持 8/16 字节",
        "Invalid DUKPT TIK length; TDES TIK usually supports 8/16 bytes"
    ),
    SECURITY_DUKPT_KSN_OUT_OF_RANGE(
        SecurityError.SECURITY_DUKPT_KSN_OUT_OF_RANGE,
        "SECURITY_DUKPT_KSN_OUT_OF_RANGE",
        "DUKPT KSN 长度或取值超出范围",
        "DUKPT KSN length or value is out of range"
    ),
    VENDOR_PED_WRITE_KEY_EMPTY(
        1,
        "PED_WRITE_KEY_RESULT_ERROR_EMPTY",
        "底层安全处理器返回密钥为空",
        "Secure processor reports empty key data"
    ),
    VENDOR_PED_WRITE_KEY_WRITE_FAILED(
        2,
        "PED_WRITE_KEY_RESULT_ERROR_WRITE",
        "底层安全处理器写入密钥失败",
        "Secure processor failed to write the key"
    ),
    VENDOR_PED_WRITE_KEY_CHECK_FAILED(
        3,
        "PED_WRITE_KEY_RESULT_ERROR_CHECK",
        "底层安全处理器 KCV 校验失败",
        "Secure processor KCV verification failed"
    ),
    VENDOR_PED_WRITE_TIK_REJECTED(
        65223,
        "VENDOR_PED_WRITE_TIK_REJECTED",
        "底层安全处理器拒绝写入 DUKPT TIK，通常是 TIK/KSN/KCV 组合不匹配，或 KCV 长度不符合模式要求。KCV 模式 1 需要加密全 0 后密文的前 4 字节",
        "Secure processor rejected DUKPT TIK injection, usually because TIK/KSN/KCV do not match or the KCV length does not match the mode. KCV mode 1 requires the first 4 bytes of encrypted zeros"
    );

    companion object {
        private val byCode = values().associateBy(FinancialSdkErrorCode::code)

        fun from(code: Int): FinancialSdkErrorCode? = byCode[code]
    }
}
