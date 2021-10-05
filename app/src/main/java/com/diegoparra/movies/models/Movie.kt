package com.diegoparra.movies.models

import androidx.annotation.FloatRange
import java.time.LocalDate

data class Movie(
    val id: String,
    val title: String,
    val posterUrl: String,
    val backdropUrl: String,
    val overview: String,
    val genres: List<Genre>,
    val releaseDate: LocalDate?,
    val language: String?,
    val popularity: Double?,
    @FloatRange(from = 0.0, to = 10.0) val voteAverage: Float?,
    val voteCount: Long?,
    val budget: Long?,
    val homepageUrl: String?,
    val runtimeMinutes: Int?,
    val tagline: String?,
    val status: String?
)