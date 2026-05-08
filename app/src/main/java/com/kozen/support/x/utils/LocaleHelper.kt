package com.kozen.support.x.utils

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {
    const val LANGUAGE_ZH = "zh"
    const val LANGUAGE_EN = "en"

    private const val PREFS_NAME = "kozen_locale"
    private const val KEY_LANGUAGE = "language"

    fun getLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, LANGUAGE_ZH)
            .takeIf { it == LANGUAGE_EN || it == LANGUAGE_ZH }
            ?: LANGUAGE_ZH
    }

    fun setLanguage(context: Context, language: String) {
        val normalized = if (language == LANGUAGE_EN) LANGUAGE_EN else LANGUAGE_ZH
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, normalized)
            .apply()
    }

    fun wrapContext(context: Context): Context {
        val locale = when (getLanguage(context)) {
            LANGUAGE_EN -> Locale.ENGLISH
            else -> Locale.SIMPLIFIED_CHINESE
        }
        Locale.setDefault(locale)

        val configuration = Configuration(context.resources.configuration)
        configuration.setLocale(locale)
        return context.createConfigurationContext(configuration)
    }
}
