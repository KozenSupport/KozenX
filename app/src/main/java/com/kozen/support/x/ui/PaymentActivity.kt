package com.kozen.support.x.ui

import android.app.Activity
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

class PaymentActivity : Activity() {
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
        updateStatus("Initializing Financial SDK...", "You can enter amount while initialization runs.")
        btnConfirm.isEnabled = false
    }

    private fun initFinancialSdk() {
        if (isFinancialSdkReady()) {
            ensureDemoKeysLoaded()
            return
        }

        updateStatus("Initializing Financial SDK...", "This will be reused after the first successful init.")
        FinancialEngine.init(this) { code, msg ->
            if (code == 0) {
                ensureDemoKeysLoaded()
            } else {
                runOnUiThread {
                    updateStatus("Financial SDK init failed.", msg ?: "Error code: $code")
                    showDialog("Init Failed", msg ?: "Financial SDK init failed: $code", finishOnOk = true)
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
            updateStatus("Preparing demo keys...", "This is only done once per app process.")
        }

        Thread {
            try {
                writeKeys()
                demoKeysLoaded = true
                runOnUiThread { markSdkReady() }
            } catch (e: Exception) {
                Log.e(tag, "Failed to write demo keys", e)
                runOnUiThread {
                    updateStatus("Demo key loading failed.", e.message.orEmpty())
                    showDialog("Init Failed", e.message ?: "Failed to write demo keys", finishOnOk = true)
                }
            }
        }.start()
    }

    private fun markSdkReady() {
        sdkReady = true
        btnConfirm.isEnabled = true
        updateStatus("Ready", "Enter amount and present card after tapping CONFIRM.")
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
            updateResult(-1, "Canceled by user")
            return
        }
        resetAmount()
    }

    private fun onConfirmClicked() {
        if (!sdkReady) {
            showDialog("Please Wait", "Financial SDK is still initializing.")
            return
        }
        if (isProcessing) {
            showDialog("Please Wait", "A transaction is already running.")
            return
        }

        val amountMinorUnits = parseAmountMinorUnits() ?: run {
            showDialog("Invalid Amount", "Please enter an amount greater than 0.")
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
            showLoading("Please present card...")
            startTransaction(
                amountMinorUnits = amountMinorUnits,
                activity = this,
                updateProgress = { message -> runOnUiThread { showTransactionProgress(message) } },
                updateResult = { code, message -> updateResult(code, message) }
            )
        } catch (e: Exception) {
            Log.e(tag, "Transaction failed before EMV start", e)
            updateResult(-1, e.message ?: "Transaction failed")
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
                showDialog("Approved", message.orEmpty().ifBlank { "Transaction successful" })
            } else {
                showDialog("Declined", message.orEmpty().ifBlank { "Transaction failed" })
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
        updateStatus(message, "Transaction is running.")
        if (message.contains("PIN", ignoreCase = true)) {
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
            .setPositiveButton("OK") { dialog, _ ->
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
