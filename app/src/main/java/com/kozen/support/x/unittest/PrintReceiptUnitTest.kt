package com.kozen.support.x.unittest

import android.annotation.SuppressLint
import android.content.Context
import com.kozen.financial.aidl.printer.BarcodePrintLine
import com.kozen.financial.aidl.printer.TextPrintLine
import com.kozen.financial.constant.ConstantPrinter
import com.kozen.financial.engine.FinancialEngine
import com.kozen.financial.printer.IPrintResultCallback
import com.kozen.financial.printer.IPrinterManager
import com.kozen.support.x.R
import com.kozen.support.x.utils.FinancialSdkErrorTranslator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PrintReceiptUnitTest : UnitTestCase {
    private const val SUCCESS = 0
    private const val RECEIPT_WIDTH = 32

    @Volatile
    private var isPrinting = false

    override fun runningMessage(context: Context): String {
        return context.getString(R.string.unit_test_print_running)
    }

    override fun successMessage(context: Context): String {
        return context.getString(R.string.unit_test_print_success)
    }

    override fun start(context: Context, callback: UnitTestCallback) {
        if (isPrinting) {
            callback.onFailure(context.getString(R.string.unit_test_print_already_running))
            return
        }

        isPrinting = true
        val appContext = context.applicationContext
        if (FinancialEngine.printerManager != null) {
            printReceipt(appContext, callback)
            return
        }

        FinancialEngine.init(appContext) { code, msg ->
            if (code == SUCCESS) {
                printReceipt(appContext, callback)
            } else {
                fail(
                    appContext,
                    callback,
                    appContext.getString(
                        R.string.unit_test_print_failed_init,
                        msg ?: FinancialSdkErrorTranslator.describe(appContext, code)
                    )
                )
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun printReceipt(context: Context, callback: UnitTestCallback) {
        Thread {
            val printer = FinancialEngine.printerManager
            if (printer == null) {
                fail(context, callback, context.getString(R.string.unit_test_print_failed_manager))
                return@Thread
            }

            try {
                val openResult = printer.open()
                if (openResult != SUCCESS) {
                    fail(
                        context,
                        callback,
                        context.getString(
                            R.string.unit_test_print_failed_start,
                            FinancialSdkErrorTranslator.formatFailure(context, "open printer", openResult)
                        )
                    )
                    return@Thread
                }

                printer.setClearPrintCacheOnPaperOut(true)
                printer.setLineSpace(2)
                printer.setGray(2000)
                addReceiptContent(printer)

                val startResult = printer.startPrint(object : IPrintResultCallback {
                    override fun onFinish() {
                        printer.close()
                        isPrinting = false
                        callback.onSuccess()
                    }

                    override fun onError(error: Int, msg: String?) {
                        printer.close()
                        fail(
                            context,
                            callback,
                            context.getString(
                                R.string.unit_test_print_failed_start,
                                msg?.takeIf { it.isNotBlank() }
                                    ?: FinancialSdkErrorTranslator.describe(context, error)
                            )
                        )
                    }
                })

                if (startResult != SUCCESS) {
                    printer.close()
                    fail(
                        context,
                        callback,
                        context.getString(
                            R.string.unit_test_print_failed_start,
                            FinancialSdkErrorTranslator.formatFailure(context, "start print", startResult)
                        )
                    )
                }
            } catch (e: Exception) {
                runCatching { printer.close() }
                fail(
                    context,
                    callback,
                    context.getString(R.string.unit_test_print_failed_exception, e.message.orEmpty())
                )
            }
        }.start()
    }

    private fun addReceiptContent(printer: IPrinterManager) {
        val orderNo = "KZ${System.currentTimeMillis().toString().takeLast(10)}"
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())
        val qrPayload = "https://kozen.example/receipt/$orderNo"

        addText(printer, "KOZEN MART", ConstantPrinter.Align.CENTER, ConstantPrinter.FontSize.LARGE, true)
        addText(printer, "Demo Cashier Receipt", ConstantPrinter.Align.CENTER, ConstantPrinter.FontSize.NORMAL, false)
        addText(printer, "Shanghai Store 001", ConstantPrinter.Align.CENTER, ConstantPrinter.FontSize.SMALL, false)
        addSeparator(printer)
        addText(printer, twoColumn("Order", orderNo))
        addText(printer, twoColumn("Time", now))
        addText(printer, twoColumn("Cashier", "UnitTest"))
        addText(printer, twoColumn("Terminal", "KZ-POS-01"))
        addSeparator(printer)
        addText(printer, "Item               Qty    Amt", ConstantPrinter.Align.LEFT, ConstantPrinter.FontSize.NORMAL, true)
        addText(printer, row("Coffee", 2, "18.00"))
        addText(printer, row("Sandwich", 1, "22.00"))
        addText(printer, row("Mineral Water", 3, "9.00"))
        addSeparator(printer)
        addText(printer, twoColumn("Subtotal", "49.00"))
        addText(printer, twoColumn("Discount", "-4.90"))
        addText(printer, twoColumn("Total CNY", "44.10"), ConstantPrinter.Align.LEFT, ConstantPrinter.FontSize.LARGE, true)
        addSeparator(printer)
        addText(printer, twoColumn("Payment", "CARD"))
        addText(printer, twoColumn("Card", "6222 **** **** 1234"))
        addText(printer, twoColumn("Auth", "A12345"))
        printer.wrapLine(1)
        addText(printer, "Scan for e-receipt", ConstantPrinter.Align.CENTER, ConstantPrinter.FontSize.NORMAL, false)
        printer.addBarcode(BarcodePrintLine(qrPayload, 240, 240, ConstantPrinter.BarcodeFormat.QR_CODE))
        printer.wrapLine(1)
        printer.addBarcode(
            BarcodePrintLine(
                orderNo,
                ConstantPrinter.Align.CENTER,
                96,
                384,
                ConstantPrinter.BarcodeFormat.CODE_128
            )
        )
        addText(printer, orderNo, ConstantPrinter.Align.CENTER, ConstantPrinter.FontSize.SMALL, false)
        addSeparator(printer)
        addText(printer, "Thank you. Please come again.", ConstantPrinter.Align.CENTER, ConstantPrinter.FontSize.NORMAL, false)
        printer.wrapLine(4)
    }

    private fun addText(
        printer: IPrinterManager,
        content: String,
        align: ConstantPrinter.Align = ConstantPrinter.Align.LEFT,
        fontSize: Float = ConstantPrinter.FontSize.NORMAL,
        bold: Boolean = false
    ) {
        printer.addText(TextPrintLine(content, align, fontSize, bold))
    }

    private fun addSeparator(printer: IPrinterManager) {
        addText(printer, "-".repeat(RECEIPT_WIDTH))
    }

    private fun twoColumn(left: String, right: String): String {
        val spaces = (RECEIPT_WIDTH - left.length - right.length).coerceAtLeast(1)
        return left + " ".repeat(spaces) + right
    }

    private fun row(name: String, quantity: Int, amount: String): String {
        val item = name.take(16).padEnd(16)
        val qty = quantity.toString().padStart(3)
        val price = amount.padStart(9)
        return "$item $qty $price"
    }

    private fun fail(context: Context, callback: UnitTestCallback, message: String) {
        isPrinting = false
        callback.onFailure(message.ifBlank { context.getString(R.string.unit_test_print_failed_unknown) })
    }
}
