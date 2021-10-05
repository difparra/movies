package com.diegoparra.movies.data

import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Either

interface MoviesRepository {

    suspend fun getPopularMovies(): Either<Exception, List<Movie>>
    suspend fun getMovieById(id: String): Either<Exception, Movie>

}