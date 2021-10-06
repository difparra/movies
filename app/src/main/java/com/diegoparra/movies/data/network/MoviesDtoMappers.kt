package com.diegoparra.movies.data.network

import com.diegoparra.movies.data.network.dtos.MovieListItemDto
import com.diegoparra.movies.data.network.dtos.MovieResponse
import com.diegoparra.movies.data.network.dtos.MoviesListResponse
import com.diegoparra.movies.models.Movie

interface MoviesDtoMappers {

    fun toMoviesList(moviesListResponse: MoviesListResponse): List<Movie>
    fun toMovie(movieListItemDto: MovieListItemDto): Movie
    fun toMovie(movieResponse: MovieResponse): Movie

}