package com.kozen.support.x.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.TextView
import com.kozen.financial.constant.ConstantEmv.POIEmvCoreManager.EmvPinConstraints
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.pinpad.PinViewEnum
import com.kozen.financial.pinpad.PinpadInputCallback
import com.kozen.support.x.R
import com.kozen.support.x.config.DeviceConfig

class PinInputDialog(
    context: Context,
    private val pinParams: Bundle
) : Dialog(context, R.style.PinDialogTheme) {

    private var callback: PinInputCallback? = null
    private var maxPinLength = 12
    private var isDismissing = false
    private var cancelOnDismiss = false
    private var finished = false
    private var pinStarted = false
    private var layoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null

    private lateinit var tvPinDisplay: TextView
    private lateinit var btnCancel: Button

    interface PinInputCallback {
        fun onPinSuccess(verifyResult: Int, pinBlock: ByteArray, ksn: String?)
        fun onPinError(verifyResult: Int, pinTryCntOut: Int)
        fun onCancel()
    }

    fun setPinInputCallback(callback: PinInputCallback) {
        this.callback = callback
    }

    override fun show() {
        if (USE_SDK_DEFAULT_PINPAD) {
            startDefaultPinInput()
            return
        }
        super.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_pin_input)
        setCanceledOnTouchOutside(false)
        setCancelable(false)

        tvPinDisplay = findViewById(R.id.tv_pin_display)
        btnCancel = findViewById(R.id.btn_cancel)
        installCancelClick()
    }

    override fun onStart() {
        super.onStart()
        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.92f).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        waitForPinpadLayout()
    }

    private fun startPinInputAfterLayout() {
        if (pinStarted || finished || !isShowing) return
        removeLayoutListener()
        pinStarted = true

        val pinpadManager = FinancialEngine.pinpadManager
        if (pinpadManager == null) {
            finishWithPinError(EmvPinConstraints.VERIFY_ERROR, 0)
            return
        }

        val effectivePinParams = buildEffectivePinParams()
        maxPinLength = resolveMaxPinLength(effectivePinParams)

        val keyViews = mapOf(
            PinViewEnum.BUTTON0.type to findViewById<Button>(R.id.btn_0),
            PinViewEnum.BUTTON1.type to findViewById<Button>(R.id.btn_1),
            PinViewEnum.BUTTON2.type to findViewById<Button>(R.id.btn_2),
            PinViewEnum.BUTTON3.type to findViewById<Button>(R.id.btn_3),
            PinViewEnum.BUTTON4.type to findViewById<Button>(R.id.btn_4),
            PinViewEnum.BUTTON5.type to findViewById<Button>(R.id.btn_5),
            PinViewEnum.BUTTON6.type to findViewById<Button>(R.id.btn_6),
            PinViewEnum.BUTTON7.type to findViewById<Button>(R.id.btn_7),
            PinViewEnum.BUTTON8.type to findViewById<Button>(R.id.btn_8),
            PinViewEnum.BUTTON9.type to findViewById<Button>(R.id.btn_9),
            PinViewEnum.BUTTON_ENTER.type to findViewById<Button>(R.id.btn_enter),
            PinViewEnum.BUTTON_BACKSPACE.type to findViewById<Button>(R.id.btn_clear),
            PinViewEnum.BUTTON_ESC.type to btnCancel
        )

        Log.w(TAG, "startInputPin params=${bundleToDebugString(effectivePinParams)}")
        Log.w(TAG, "startInputPin keyBounds=${keyBoundsDebugString(keyViews.values)}")
        pinpadManager.startInputPin(effectivePinParams, keyViews, createPinpadCallback())

        // Some SDK builds attach their own listeners during cacheViews(); keep the
        // demo cancel button application-owned so it can always close the dialog.
        btnCancel.post { installCancelClick() }
    }

    private fun startDefaultPinInput() {
        if (pinStarted || finished) return
        pinStarted = true

        val pinpadManager = FinancialEngine.pinpadManager
        if (pinpadManager == null) {
            finishWithPinError(EmvPinConstraints.VERIFY_ERROR, 0)
            return
        }

        val effectivePinParams = buildEffectivePinParams()
        maxPinLength = resolveMaxPinLength(effectivePinParams)

        Log.w(TAG, "startInputPin(default) params=${bundleToDebugString(effectivePinParams)}")
        pinpadManager.startInputPin(effectivePinParams, createPinpadCallback())
    }

    private fun createPinpadCallback(): PinpadInputCallback {
        return object : PinpadInputCallback {
            override fun onInput(len: Int, key: Int) {
                Log.w(TAG, "onInput len=$len")
                if (::tvPinDisplay.isInitialized && len <= maxPinLength) {
                    tvPinDisplay.text = "*".repeat(len)
                }
            }

            override fun onPinError(verifyResult: Int, pinTryCntOut: Int) {
                Log.w(TAG, "onPinError result=$verifyResult try=$pinTryCntOut")
                when (verifyResult) {
                    EmvPinConstraints.VERIFY_CANCELED,
                    EmvPinConstraints.VERIFY_MANUALLY_CANCELED -> finishWithCancel(cancelPinpad = false)
                    else -> finishWithPinError(verifyResult, pinTryCntOut)
                }
            }

            override fun onPinSuccess(verifyResult: Int, pinBlock: ByteArray, ksn: String?) {
                if (finished) return
                finished = true
                callback?.onPinSuccess(verifyResult, pinBlock, ksn)
                dismiss()
            }

            override fun onScreenRotation() = Unit
        }
    }

    private fun installCancelClick() {
        btnCancel.setOnClickListener {
            finishWithCancel()
        }
    }

    private fun finishWithCancel(cancelPinpad: Boolean = true) {
        if (finished) return
        finished = true
        cancelOnDismiss = cancelPinpad
        dismiss()
        callback?.onCancel()
    }

    private fun finishWithPinError(verifyResult: Int, pinTryCntOut: Int) {
        if (finished) return
        finished = true
        callback?.onPinError(verifyResult, pinTryCntOut)
        dismiss()
    }

    private fun buildEffectivePinParams(): Bundle {
        return Bundle(pinParams).apply {
            if (!containsKey(EmvPinConstraints.PIN_KEY_INDEX) ||
                getInt(EmvPinConstraints.PIN_KEY_INDEX, INVALID_PIN_KEY_INDEX) <= INVALID_PIN_KEY_INDEX
            ) {
                putInt(EmvPinConstraints.PIN_KEY_INDEX, DeviceConfig.pinKeyIndex)
            }
            if (!containsKey(EmvPinConstraints.PIN_CARD)) {
                putString(EmvPinConstraints.PIN_CARD, DEFAULT_PIN_CARD)
            }
            if (!containsKey(EmvPinConstraints.PIN_BLOCK_FORMAT)) {
                putInt(EmvPinConstraints.PIN_BLOCK_FORMAT, EmvPinConstraints.PIN_ISO_FMT0)
            }
            if (!containsKey(EmvPinConstraints.PIN_KEY_MODE)) {
                putInt(EmvPinConstraints.PIN_KEY_MODE, EmvPinConstraints.PIN_KEY_MODE_TPK)
            }
            if (!containsKey(EmvPinConstraints.PIN_KEY_ALGORITHM)) {
                putInt(EmvPinConstraints.PIN_KEY_ALGORITHM, EmvPinConstraints.PIN_KEY_ALGORITHM_TDES)
            }
            if (!containsKey(EmvPinConstraints.PIN_TIMEOUT) ||
                getInt(EmvPinConstraints.PIN_TIMEOUT, 0) !in MIN_PIN_TIMEOUT_SECONDS..MAX_PIN_TIMEOUT_SECONDS
            ) {
                putInt(EmvPinConstraints.PIN_TIMEOUT, PIN_TIMEOUT_SECONDS)
            }
            putByteArray(EmvPinConstraints.PIN_LENGTH_LIMIT, resolvePinLengthLimit(this))
            if (!containsKey(EmvPinConstraints.PIN_IS_ORDER)) {
                putBoolean(EmvPinConstraints.PIN_IS_ORDER, DeviceConfig.isPinpadKeyOrderly)
            }
            if (!containsKey(EmvPinConstraints.PIN_BYPASS)) {
                putBoolean(EmvPinConstraints.PIN_BYPASS, DeviceConfig.pinByPass)
            }
        }
    }

    private fun resolvePinLengthLimit(bundle: Bundle): ByteArray {
        return when (val value = bundle.get(EmvPinConstraints.PIN_LENGTH_LIMIT)) {
            is ByteArray -> value.takeIf { it.isNotEmpty() } ?: DEFAULT_PIN_LENGTH_LIMIT
            is Int -> buildPinLengthLimit(value)
            else -> DEFAULT_PIN_LENGTH_LIMIT
        }
    }

    private fun resolveMaxPinLength(bundle: Bundle): Int {
        return (bundle.get(EmvPinConstraints.PIN_LENGTH_LIMIT) as? ByteArray)
            ?.map { it.toInt() and 0xFF }
            ?.filter { it > 0 }
            ?.maxOrNull()
            ?: 12
    }

    private fun buildPinLengthLimit(maxLength: Int): ByteArray {
        val normalizedMax = maxLength.coerceIn(MIN_PIN_LENGTH, MAX_PIN_LENGTH)
        return byteArrayOf(0) + (MIN_PIN_LENGTH..normalizedMax).map { it.toByte() }.toByteArray()
    }

    override fun dismiss() {
        if (isDismissing) return
        isDismissing = true
        removeLayoutListener()
        if (cancelOnDismiss) {
            FinancialEngine.pinpadManager?.cancelInputPin()
        }
        if (isShowing) {
            super.dismiss()
        }
    }

    private fun waitForPinpadLayout() {
        val decorView = window?.decorView ?: return
        if (arePinpadKeysLaidOut()) {
            startPinInputAfterLayout()
            return
        }

        val listener = ViewTreeObserver.OnGlobalLayoutListener {
            if (arePinpadKeysLaidOut()) {
                startPinInputAfterLayout()
            }
        }
        layoutListener = listener
        decorView.viewTreeObserver.addOnGlobalLayoutListener(listener)
        decorView.postDelayed({
            if (!pinStarted && !finished && isShowing) {
                Log.w(TAG, "PIN key layout was not ready; starting with current bounds")
                startPinInputAfterLayout()
            }
        }, MAX_PIN_LAYOUT_WAIT_MS)
    }

    private fun arePinpadKeysLaidOut(): Boolean {
        return PIN_KEY_VIEW_IDS.all { id ->
            val view = findViewById<View>(id)
            view != null && view.isShown && view.width > 0 && view.height > 0
        }
    }

    private fun removeLayoutListener() {
        val listener = layoutListener ?: return
        val observer = window?.decorView?.viewTreeObserver
        if (observer?.isAlive == true) {
            observer.removeOnGlobalLayoutListener(listener)
        }
        layoutListener = null
    }

    private fun bundleToDebugString(bundle: Bundle): String {
        return bundle.keySet().joinToString(prefix = "{", postfix = "}") { key ->
            val value = bundle.get(key)
            val display = when (value) {
                is ByteArray -> "ByteArray(${value.size})"
                else -> value.toString()
            }
            "$key=$display"
        }
    }

    private fun keyBoundsDebugString(views: Collection<View>): String {
        return views.joinToString(prefix = "[", postfix = "]") { view ->
            "${view.resources.getResourceEntryName(view.id)}=${view.width}x${view.height}"
        }
    }

    private companion object {
        const val TAG = "PaymentDemo.Pin"
        const val USE_SDK_DEFAULT_PINPAD = true
        const val MAX_PIN_LAYOUT_WAIT_MS = 500L
        const val PIN_TIMEOUT_SECONDS = 60
        const val MIN_PIN_TIMEOUT_SECONDS = 1
        const val MAX_PIN_TIMEOUT_SECONDS = 600
        const val INVALID_PIN_KEY_INDEX = 0
        const val MIN_PIN_LENGTH = 4
        const val MAX_PIN_LENGTH = 12
        const val DEFAULT_PIN_CARD = "0000000000000000"
        val DEFAULT_PIN_LENGTH_LIMIT = byteArrayOf(0, 4, 5, 6, 7, 8, 9, 10, 11, 12)
        val PIN_KEY_VIEW_IDS = intArrayOf(
            R.id.btn_0,
            R.id.btn_1,
            R.id.btn_2,
            R.id.btn_3,
            R.id.btn_4,
            R.id.btn_5,
            R.id.btn_6,
            R.id.btn_7,
            R.id.btn_8,
            R.id.btn_9,
            R.id.btn_clear,
            R.id.btn_enter,
            R.id.btn_cancel
        )
    }
}
