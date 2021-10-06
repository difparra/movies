package com.diegoparra.movies.data.local.entities

import androidx.annotation.FloatRange
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity(tableName = "Movie")
data class MovieEntity(
    @PrimaryKey val movieId: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val overview: String,
    val genres: List<GenreDb>,
    val releaseDate: LocalDate?,
    val language: Locale?,
    val popularity: Double?,
    @FloatRange(from = 0.0, to = 10.0) val voteAverage: Float?,
    val voteCount: Long?,
    val budget: Long?,
    val homepageUrl: String?,
    val runtimeMinutes: Int?,
    val tagline: String?,
    val status: String?,
    val updatedAt: Instant
)