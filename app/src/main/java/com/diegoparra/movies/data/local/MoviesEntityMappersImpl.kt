package com.diegoparra.movies.data.local

import com.diegoparra.movies.data.local.entities.GenreDb
import com.diegoparra.movies.data.local.entities.MovieEntity
import com.diegoparra.movies.models.Genre
import com.diegoparra.movies.models.Movie

object MoviesEntityMappersImpl: MoviesEntityMappers {

    override fun toMovie(movieEntity: MovieEntity): Movie = with(movieEntity) {
        Movie(
            id = movieId,
            title = title,
            posterUrl = posterUrl,
            backdropUrl = backdropUrl,
            overview = overview,
            genres = genres.map { it.toGenre() },
            releaseDate = releaseDate,
            language = language,
            popularity = popularity,
            voteAverage = voteAverage,
            voteCount = voteCount,
            budget = budget,
            homepageUrl = homepageUrl,
            runtimeMinutes = runtimeMinutes,
            tagline = tagline,
            status = status
        )
    }

    private fun GenreDb.toGenre(): Genre = Genre(id = genreId, name = name)

}