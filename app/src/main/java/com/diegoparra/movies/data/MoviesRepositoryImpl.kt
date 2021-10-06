package com.diegoparra.movies.data

import android.util.Log
import com.diegoparra.movies.data.local.MoviesDao
import com.diegoparra.movies.data.local.MoviesEntityMappers
import com.diegoparra.movies.data.local.entities.MovieEntity
import com.diegoparra.movies.data.network.MoviesApi
import com.diegoparra.movies.data.network.MoviesDtoMappers
import com.diegoparra.movies.di.IoDispatcher
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Either
import com.diegoparra.movies.utils.runCatching
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.time.Instant
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MoviesRepositoryImpl @Inject constructor(
    private val api: MoviesApi,
    private val dtoMappers: MoviesDtoMappers,
    private val dao: MoviesDao,
    private val entityMappers: MoviesEntityMappers,
    private val dtoToEntityMappers: MoviesDtoToEntityMappers,
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
                val movieLocal = dao.getMovieById(id)
                if (movieLocal != null && isMovieUpdated(movieLocal)) {
                    Timber.d("Data collected from local persistence, movie = ${movieLocal.movieId}, ${movieLocal.title}")
                    return@runCatching entityMappers.toMovie(movieLocal)
                } else {
                    val movieDto = api.getMovieById(id)
                    dao.insertMovie(dtoToEntityMappers.movieDtoResponseToMovieEntity(movieDto))
                    val updatedLocalMovie = dao.getMovieById(id)!!
                    Timber.d("Data collected from api and saved, movie = ${updatedLocalMovie.movieId}, ${updatedLocalMovie.title}")
                    return@runCatching entityMappers.toMovie(updatedLocalMovie)
                }
            }
        }

    private fun isMovieUpdated(movieLocal: MovieEntity): Boolean {
        val millisFromLastUpdate =
            Instant.now().toEpochMilli() - movieLocal.updatedAt.toEpochMilli()
        return millisFromLastUpdate <= TimeUnit.DAYS.toMillis(1)
    }

}