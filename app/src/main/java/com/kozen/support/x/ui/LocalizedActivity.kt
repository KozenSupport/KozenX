package com.kozen.support.x.ui

import android.app.Activity
import android.content.Context
import com.kozen.support.x.utils.LocaleHelper

open class LocalizedActivity : Activity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }
}
