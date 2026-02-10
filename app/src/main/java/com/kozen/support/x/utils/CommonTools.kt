package com.kozen.support.x.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

object CommonTools {

    fun showMethodDialog(context : Context, title: String, message: String) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }


}