package com.diegoparra.movies.data.network

import com.diegoparra.movies.data.network.dtos.GenreDto
import com.diegoparra.movies.data.network.dtos.MovieListItemDto
import com.diegoparra.movies.data.network.dtos.MovieResponse
import com.diegoparra.movies.data.network.dtos.MoviesListResponse
import com.diegoparra.movies.models.Genre
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.LocalDateUtils
import com.diegoparra.movies.utils.LocaleUtils
import timber.log.Timber
import java.time.LocalDate
import java.util.*

object MoviesDtoMappersImpl : MoviesDtoMappers {

    override fun toMoviesList(moviesListResponse: MoviesListResponse): List<Movie> {
        return moviesListResponse.results.map { toMovie(it) }
    }

    override fun toMovie(movieListItemDto: MovieListItemDto): Movie = with(movieListItemDto) {
        Movie(
            id = id,
            title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
            posterUrl = posterPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            backdropUrl = backdropPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            overview = overview ?: "",
            genres = genreIds?.map { Genre(it, "") } ?: emptyList(),
            releaseDate = LocalDateUtils.parseOrNull(releaseDate),
            language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
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
            releaseDate = LocalDateUtils.parseOrNull(releaseDate),
            language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
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

}