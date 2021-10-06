package com.diegoparra.movies.data.local

import androidx.room.*
import com.diegoparra.movies.data.local.entities.MovieEntity

@Dao
interface MoviesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Query("SELECT * FROM Movie WHERE movieId = :movieId")
    suspend fun getMovieById(movieId: String): MovieEntity?

}