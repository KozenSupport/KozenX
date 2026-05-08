package com.kozen.support.x.utils

import android.content.Context
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kozen.support.x.R
import com.kozen.support.x.config.SdkTypeConstants

object CommonTools {

    fun showMethodDialog(context: Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(context.getString(R.string.common_ok), null)
            .show()
    }

    fun showHowToIntegrateMDM(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.integrate_title))
            .setMessage(context.getString(R.string.integrate_mdm))
            .setPositiveButton(context.getString(R.string.common_ok), null)
            .show()
    }

    fun showHowToIntegrateComponent(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.integrate_title))
            .setMessage(context.getString(R.string.integrate_component))
            .setPositiveButton(context.getString(R.string.common_ok), null)
            .show()
    }

    fun showHowToIntegrateTerminal(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.integrate_title))
            .setMessage(context.getString(R.string.integrate_terminal))
            .setPositiveButton(context.getString(R.string.common_ok), null)
            .show()
    }

    fun showHowToIntegrateFincial(context: Context) {
        AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.integrate_title))
            .setMessage(context.getString(R.string.integrate_financial))
            .setPositiveButton(context.getString(R.string.common_ok), null)
            .show()
    }

    fun showHowToIntegrate(sdkType: String?, context: Context) {
        when (sdkType) {
            SdkTypeConstants.MDM -> showHowToIntegrateMDM(context)
            SdkTypeConstants.COMPONENT -> showHowToIntegrateComponent(context)
            SdkTypeConstants.TERMINAL -> showHowToIntegrateTerminal(context)
            SdkTypeConstants.FINANCIAL -> showHowToIntegrateFincial(context)
            else -> showMethodDialog(
                context,
                context.getString(R.string.common_error),
                context.getString(R.string.sdk_type_config_error)
            )
        }
    }

    fun TextView.setTextWithColors(vararg parts: Pair<String, Int>) {
        val spannable = SpannableStringBuilder()
        parts.forEach { (text, color) ->
            val start = spannable.length
            spannable.append(text)
            spannable.setSpan(
                ForegroundColorSpan(color),
                start,
                spannable.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        this.text = spannable
    }
}
