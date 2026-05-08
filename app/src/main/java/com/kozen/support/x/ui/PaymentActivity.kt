package com.kozen.support.x.ui

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.kozen.financial.constant.ConstantCardReader
import com.kozen.financial.engine.FinancialEngine
import com.kozen.support.x.R
import com.kozen.support.x.utils.startTransaction
import com.kozen.support.x.utils.writeKeys
import java.math.BigDecimal
import java.math.RoundingMode

class PaymentActivity : LocalizedActivity() {
    private val tag = "PaymentDemo"

    private lateinit var tvAmount: TextView
    private lateinit var tvStatus: TextView
    private lateinit var tvHint: TextView
    private lateinit var btnConfirm: Button
    private lateinit var layoutContent: View
    private lateinit var layoutLoading: View
    private lateinit var tvLoadingMsg: TextView

    private val amountInput = StringBuilder("0")
    private var hasDecimal = false
    private var decimalCount = 0
    private var isProcessing = false
    private var sdkReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        initViews()
        initFinancialSdk()
    }

    override fun onDestroy() {
        FinancialEngine.emvManager?.stopTransaction()
        FinancialEngine.cardReaderManager?.powerOff(ConstantCardReader.CardType.MAGNETIC)
        super.onDestroy()
    }

    private fun initViews() {
        layoutContent = findViewById(R.id.layout_content)
        layoutLoading = findViewById(R.id.layout_loading)
        tvLoadingMsg = findViewById(R.id.tv_loading_msg)
        tvAmount = findViewById(R.id.tv_amount)
        tvStatus = findViewById(R.id.tv_message1)
        tvHint = findViewById(R.id.tv_message2)
        btnConfirm = findViewById(R.id.btn_confirm)

        setupNumpad()
        findViewById<View>(R.id.btn_back).setOnClickListener { finish() }
        findViewById<View>(R.id.btn_cancel).setOnClickListener { onCancelClicked() }
        btnConfirm.setOnClickListener { onConfirmClicked() }
        showMainContent()
        updateStatus(
            getString(R.string.payment_status_sdk_initializing),
            getString(R.string.payment_hint_enter_while_init)
        )
        btnConfirm.isEnabled = false
    }

    private fun initFinancialSdk() {
        if (isFinancialSdkReady()) {
            ensureDemoKeysLoaded()
            return
        }

        updateStatus(
            getString(R.string.payment_status_sdk_initializing),
            getString(R.string.payment_hint_reused_after_init)
        )
        FinancialEngine.init(this) { code, msg ->
            if (code == 0) {
                ensureDemoKeysLoaded()
            } else {
                runOnUiThread {
                    updateStatus(getString(R.string.payment_status_sdk_init_failed), msg ?: "Error code: $code")
                    showDialog(
                        getString(R.string.payment_dialog_init_failed),
                        msg ?: "${getString(R.string.payment_status_sdk_init_failed)} $code",
                        finishOnOk = true
                    )
                }
            }
        }
    }

    private fun ensureDemoKeysLoaded() {
        if (demoKeysLoaded) {
            runOnUiThread { markSdkReady() }
            return
        }

        runOnUiThread {
            updateStatus(
                getString(R.string.payment_status_preparing_keys),
                getString(R.string.payment_hint_keys_once)
            )
        }

        Thread {
            try {
                writeKeys()
                demoKeysLoaded = true
                runOnUiThread { markSdkReady() }
            } catch (e: Exception) {
                Log.e(tag, "Failed to write demo keys", e)
                runOnUiThread {
                    updateStatus(getString(R.string.payment_status_key_loading_failed), e.message.orEmpty())
                    showDialog(
                        getString(R.string.payment_dialog_init_failed),
                        e.message ?: getString(R.string.payment_msg_key_loading_failed),
                        finishOnOk = true
                    )
                }
            }
        }.start()
    }

    private fun markSdkReady() {
        sdkReady = true
        btnConfirm.isEnabled = true
        updateStatus(
            getString(R.string.payment_status_ready),
            getString(R.string.payment_hint_ready)
        )
    }

    private fun isFinancialSdkReady(): Boolean {
        return FinancialEngine.emvManager != null &&
            FinancialEngine.cardReaderManager != null &&
            FinancialEngine.securityManager != null &&
            FinancialEngine.pinpadManager != null
    }

    private fun setupNumpad() {
        mapOf(
            R.id.btn_0 to 0,
            R.id.btn_1 to 1,
            R.id.btn_2 to 2,
            R.id.btn_3 to 3,
            R.id.btn_4 to 4,
            R.id.btn_5 to 5,
            R.id.btn_6 to 6,
            R.id.btn_7 to 7,
            R.id.btn_8 to 8,
            R.id.btn_9 to 9
        ).forEach { (id, digit) ->
            findViewById<Button>(id).setOnClickListener { appendDigit(digit) }
        }

        findViewById<Button>(R.id.btn_dot).setOnClickListener { appendDecimal() }
        findViewById<Button>(R.id.btn_del).setOnClickListener { deleteDigit() }
    }

    private fun appendDigit(digit: Int) {
        if (isProcessing) return
        if (hasDecimal && decimalCount >= 2) return

        val current = amountInput.toString()
        if (!hasDecimal && current == "0") {
            if (digit != 0) {
                amountInput.clear()
                amountInput.append(digit)
            }
        } else {
            val integerLength = current.substringBefore('.').length
            if (!hasDecimal && integerLength >= MAX_INTEGER_DIGITS) return
            amountInput.append(digit)
        }

        if (hasDecimal) decimalCount++
        updateAmountDisplay()
    }

    private fun appendDecimal() {
        if (isProcessing || hasDecimal) return
        amountInput.append('.')
        hasDecimal = true
        updateAmountDisplay()
    }

    private fun deleteDigit() {
        if (isProcessing) return
        if (amountInput.length <= 1) {
            resetAmount()
            return
        }

        val removed = amountInput.last()
        amountInput.deleteCharAt(amountInput.length - 1)
        when {
            removed == '.' -> {
                hasDecimal = false
                decimalCount = 0
            }
            hasDecimal -> decimalCount--
        }
        if (amountInput.isEmpty()) amountInput.append('0')
        updateAmountDisplay()
    }

    private fun resetAmount() {
        amountInput.clear()
        amountInput.append('0')
        hasDecimal = false
        decimalCount = 0
        updateAmountDisplay()
    }

    private fun updateAmountDisplay() {
        tvAmount.text = amountInput.toString()
    }

    private fun onCancelClicked() {
        if (isProcessing) {
            FinancialEngine.emvManager?.stopTransaction()
            FinancialEngine.cardReaderManager?.powerOff(ConstantCardReader.CardType.MAGNETIC)
            updateResult(-1, getString(R.string.payment_error_canceled_by_user))
            return
        }
        resetAmount()
    }

    private fun onConfirmClicked() {
        if (!sdkReady) {
            showDialog(
                getString(R.string.payment_dialog_please_wait),
                getString(R.string.payment_msg_sdk_initializing)
            )
            return
        }
        if (isProcessing) {
            showDialog(
                getString(R.string.payment_dialog_please_wait),
                getString(R.string.payment_msg_transaction_running)
            )
            return
        }

        val amountMinorUnits = parseAmountMinorUnits() ?: run {
            showDialog(
                getString(R.string.payment_dialog_invalid_amount),
                getString(R.string.payment_msg_invalid_amount)
            )
            return
        }

        start(amountMinorUnits)
    }

    private fun parseAmountMinorUnits(): Long? {
        val amount = amountInput.toString().toBigDecimalOrNull() ?: return null
        if (amount <= BigDecimal.ZERO) return null
        return amount
            .movePointRight(CURRENCY_EXPONENT)
            .setScale(0, RoundingMode.UNNECESSARY)
            .longValueExact()
    }

    private fun start(amountMinorUnits: Long) {
        try {
            isProcessing = true
            btnConfirm.isEnabled = false
            showLoading(getString(R.string.payment_loading_present_card))
            startTransaction(
                amountMinorUnits = amountMinorUnits,
                activity = this,
                updateProgress = { message -> runOnUiThread { showTransactionProgress(message) } },
                updateResult = { code, message -> updateResult(code, message) }
            )
        } catch (e: Exception) {
            Log.e(tag, "Transaction failed before EMV start", e)
            updateResult(-1, e.message ?: getString(R.string.payment_error_transaction_failed))
        }
    }

    fun updateResult(code: Int, message: String?) {
        runOnUiThread {
            if (isFinishing || isDestroyed) return@runOnUiThread
            isProcessing = false
            btnConfirm.isEnabled = sdkReady
            hideLoading()
            resetAmount()
            if (code == 0) {
                showDialog(
                    getString(R.string.payment_dialog_approved),
                    message.orEmpty().ifBlank { getString(R.string.payment_msg_success) }
                )
            } else {
                showDialog(
                    getString(R.string.payment_dialog_declined),
                    message.orEmpty().ifBlank { getString(R.string.payment_msg_failed) }
                )
            }
        }
    }

    private fun showMainContent() {
        layoutLoading.visibility = View.GONE
        layoutContent.visibility = View.VISIBLE
    }

    private fun updateStatus(status: String, hint: String) {
        tvStatus.text = status
        tvHint.text = hint
    }

    private fun showTransactionProgress(message: String) {
        updateStatus(message, getString(R.string.payment_hint_transaction_running))
        if (message == getString(R.string.emv_progress_enter_pin) || message.contains("PIN", ignoreCase = true)) {
            hideLoading()
        } else {
            showLoading(message)
        }
    }

    private fun showLoading(message: String) {
        tvLoadingMsg.text = message
        layoutLoading.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        layoutLoading.visibility = View.GONE
    }

    private fun showDialog(title: String, message: String, finishOnOk: Boolean = false) {
        if (isFinishing || isDestroyed) return
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(getString(R.string.common_ok)) { dialog, _ ->
                dialog.dismiss()
                if (finishOnOk) finish()
            }
            .show()
    }

    private companion object {
        const val MAX_INTEGER_DIGITS = 7
        const val CURRENCY_EXPONENT = 2
        @Volatile
        var demoKeysLoaded = false
    }
}
