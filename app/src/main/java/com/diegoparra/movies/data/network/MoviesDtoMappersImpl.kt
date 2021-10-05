package com.diegoparra.movies.data.network

import com.diegoparra.movies.data.network.dtos.GenreDto
import com.diegoparra.movies.data.network.dtos.MovieListItemDto
import com.diegoparra.movies.data.network.dtos.MovieResponse
import com.diegoparra.movies.data.network.dtos.MoviesListResponse
import com.diegoparra.movies.models.Genre
import com.diegoparra.movies.models.Movie
import timber.log.Timber
import java.time.LocalDate

object MoviesDtoMappersImpl : MoviesDtoMappers {

    override fun toMoviesList(moviesListResponse: MoviesListResponse): List<Movie> {
        return moviesListResponse.results.map { toMovie(it) }
    }

    private fun toMovie(movieListItemDto: MovieListItemDto): Movie = with(movieListItemDto) {
        Movie(
            id = id,
            title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
            posterUrl = posterPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            backdropUrl = backdropPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            overview = overview ?: "",
            genres = genreIds?.map { Genre(it, "") } ?: emptyList(),
            releaseDate = parseLocalDateOrNull(releaseDate, movieId = id),
            language = originalLanguage,
            popularity = popularity,
            voteAverage = voteAverage?.coerceIn(0f, 10f),
            voteCount = voteCount,
            budget = null,
            homepageUrl = null,
            runtimeMinutes = null,
            tagline = null,
            status = null
        )
    }

    override fun toMovie(movieResponse: MovieResponse): Movie = with(movieResponse) {
        Movie(
            id = id,
            title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
            posterUrl = posterPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            backdropUrl = backdropPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            overview = overview ?: "",
            genres = genres?.map { toGenre(it) } ?: emptyList(),
            releaseDate = parseLocalDateOrNull(releaseDate, movieId = id),
            language = originalLanguage,
            popularity = popularity,
            voteAverage = voteAverage?.coerceIn(0f, 10f),
            voteCount = voteCount,
            budget = budget,
            homepageUrl = homepageUrl,
            runtimeMinutes = runtimeMinutes,
            tagline = tagline,
            status = status
        )
    }

    private fun toGenre(genreDto: GenreDto): Genre =
        Genre(id = genreDto.id, name = genreDto.name ?: "")


    /**
     * This function is mean to avoid errors due to incorrect date formatting coming from the API.
     * If date coming from the API is not in the correct format, is empty or is null,
     * the returned date will be null without throwing more exceptions.
     */
    private fun parseLocalDateOrNull(date: String?, movieId: String? = null): LocalDate? {
        return if (date.isNullOrEmpty()) {
            null
        } else {
            try {
                LocalDate.parse(date)
            } catch (e: Exception) {
                Timber.e("Couldn't parse date: $date from movieId = $movieId")
                null
            }
        }
    }

}