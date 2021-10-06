package com.diegoparra.movies.data.local

import androidx.room.TypeConverter
import com.diegoparra.movies.data.local.entities.GenreDb
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.Instant
import java.time.LocalDate
import java.util.*

object Converters {

    /*
        It may be better to create a separate table for genres, and then a junction table to the
        many-to-many relationship between movies and genres. That way genres names can be updated
        easily, and information will not be as repeated as if it is in a single table.
        However, in order to meet the deadline and to keep the app simple, I can use only the movies
        table and save the genreList in the database by serializing and deserializing with Gson.
     */

    @TypeConverter
    fun toGenreDbList(json: String?): List<GenreDb>? {
        return if (json.isNullOrEmpty()) {
            emptyList()
        } else {
            val type = object : TypeToken<List<GenreDb>>() {}.type
            Gson().fromJson(json, type)
        }
    }

    @TypeConverter
    fun fromGenreDbList(genreList: List<GenreDb>?): String? {
        return Gson().toJson(genreList)
    }


    @TypeConverter
    fun toLocalDate(date: String?): LocalDate? {
        return if(date.isNullOrEmpty()) {
            null
        }else{
            LocalDate.parse(date)
        }
    }

    @TypeConverter
    fun fromLocalDate(date: LocalDate?): String? {
        return date.toString()
    }


    @TypeConverter
    fun toLocale(languageTag: String?): Locale? {
        return languageTag?.let { Locale.forLanguageTag(it) }
    }

    @TypeConverter
    fun fromLocale(locale: Locale?): String? {
        return locale?.toLanguageTag()
    }


    @TypeConverter
    fun toInstant(epochMilli: Long): Instant {
        return Instant.ofEpochMilli(epochMilli)
    }

    @TypeConverter
    fun fromInstant(instant: Instant): Long {
        return instant.toEpochMilli()
    }

}