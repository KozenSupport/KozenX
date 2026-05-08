package com.kozen.support.x.ui

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import com.kozen.support.x.utils.LocaleHelper

open class LocalizedAppCompatActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.wrapContext(newBase))
    }
}
