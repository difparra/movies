package com.diegoparra.movies.data.network.dtos

import com.google.gson.annotations.SerializedName

data class MovieResponse(
    val id: String,
    @SerializedName("imdb_id") val imdbId: String?,
    val adult: Boolean?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    val budget: Long?,
    val genres: List<GenreDto>?,
    @SerializedName("homepage") val homepageUrl: String?,
    @SerializedName("original_language") val originalLanguage: String?,
    @SerializedName("original_title") val originalTitle: String?,
    val overview: String?,
    val popularity: Double?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    val revenue: Long?,
    @SerializedName("runtime") val runtimeMinutes: Int?,
    val status: String?,
    val tagline: String?,
    val title: String?,
    @SerializedName("vote_average") val voteAverage: Float?,
    @SerializedName("vote_count") val voteCount: Long?
)