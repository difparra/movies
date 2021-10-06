package com.diegoparra.movies.data

import com.diegoparra.movies.data.local.entities.GenreDb
import com.diegoparra.movies.data.local.entities.MovieEntity
import com.diegoparra.movies.data.network.MoviesApi
import com.diegoparra.movies.data.network.dtos.GenreDto
import com.diegoparra.movies.data.network.dtos.MovieResponse
import com.diegoparra.movies.utils.LocalDateUtils
import com.diegoparra.movies.utils.LocaleUtils
import java.time.Instant

object MoviesDtoToEntityMappersImpl: MoviesDtoToEntityMappers {

    override fun movieDtoResponseToMovieEntity(movieResponse: MovieResponse): MovieEntity = with(movieResponse) {
        MovieEntity(
            movieId = id,
            title = if (!title.isNullOrBlank()) title else originalTitle ?: "",
            posterUrl = posterPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            backdropUrl = backdropPath?.let { MoviesApi.IMAGE_URL_PREFIX + it } ?: "",
            overview = overview ?: "",
            genres = genres?.map { it.toGenreDb() } ?: emptyList(),
            releaseDate = LocalDateUtils.parseOrNull(releaseDate),
            language = LocaleUtils.forLanguageTagOrNull(originalLanguage),
            popularity = popularity,
            voteAverage = voteAverage?.coerceIn(0f, 10f),
            voteCount = voteCount,
            budget = budget,
            homepageUrl = homepageUrl,
            runtimeMinutes = runtimeMinutes,
            tagline = tagline,
            status = status,
            updatedAt = Instant.now()
        )
    }

    private fun GenreDto.toGenreDb(): GenreDb = GenreDb(genreId = id, name = name ?: "")

}