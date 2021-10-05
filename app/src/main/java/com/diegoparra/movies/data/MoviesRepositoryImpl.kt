package com.diegoparra.movies.data

import com.diegoparra.movies.data.network.MoviesApi
import com.diegoparra.movies.data.network.MoviesDtoMappers
import com.diegoparra.movies.di.IoDispatcher
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Either
import com.diegoparra.movies.utils.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: MoviesApi,
    private val dtoMappers: MoviesDtoMappers,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : MoviesRepository {

    override suspend fun getPopularMovies(): Either<Exception, List<Movie>> =
        withContext(dispatcher) {
            Either.runCatching {
                val popularMoviesDtos = api.getPopularMovies()
                dtoMappers.toMoviesList(popularMoviesDtos)
            }
        }

    override suspend fun getMovieById(id: String): Either<Exception, Movie> =
        withContext(dispatcher) {
            Either.runCatching {
                val movieDto = api.getMovieById(id)
                dtoMappers.toMovie(movieDto)
            }
        }
}