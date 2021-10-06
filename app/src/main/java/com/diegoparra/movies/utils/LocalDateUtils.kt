package com.diegoparra.movies.utils

import timber.log.Timber
import java.time.LocalDate

/**
 * This function is meant to avoid errors due to incorrect date formatting coming from the API.
 * If date coming from the API is not in the correct format, is empty or is null,
 * the returned date will be null without throwing more exceptions.
 */
object LocalDateUtils {
    fun parseOrNull(date: String?): LocalDate? {
        return if (date.isNullOrEmpty()) {
            null
        } else {
            try {
                LocalDate.parse(date)
            } catch (e: Exception) {
                Timber.e("Couldn't parse date: $date. Exception: $e")
                null
            }
        }
    }
}