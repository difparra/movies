package com.diegoparra.movies.utils

import timber.log.Timber
import java.util.*

object LocaleUtils {
    fun forLanguageTagOrNull(languageTag: String?): Locale? {
        return if (languageTag.isNullOrEmpty()) {
            null
        } else {
            try {
                Locale.forLanguageTag(languageTag)
            } catch (e: Exception) {
                Timber.e("Couldn't parse languageTag: $languageTag. Exception: $e")
                null
            }
        }
    }
}