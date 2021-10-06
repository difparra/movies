package com.diegoparra.movies.data

import androidx.paging.PagingData
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Either
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {

    suspend fun getPopularMovies(): Either<Exception, List<Movie>>
    fun getPopularMoviesStream(): Flow<PagingData<Movie>>
    suspend fun getMovieById(id: String): Either<Exception, Movie>

}