package com.diegoparra.movies.data.network

import com.diegoparra.movies.data.network.dtos.MovieResponse
import com.diegoparra.movies.data.network.dtos.MoviesListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesApi {

    @GET("movie/popular")
    suspend fun getPopularMovies(@Query("page") page: Int = 1): MoviesListResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieById(@Path("movie_id") movieId: String): MovieResponse


    companion object {
        const val TMDB_API_KEY = "0189ad5be185d0efe0ef8b1ccd3c7462"
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_URL_PREFIX = "https://image.tmdb.org/t/p/original"
        const val PAGE_SIZE = 20
    }

}