package com.diegoparra.movies.data

import com.diegoparra.movies.data.local.entities.MovieEntity
import com.diegoparra.movies.data.network.dtos.MovieResponse

interface MoviesDtoToEntityMappers {

    fun movieDtoResponseToMovieEntity(movieResponse: MovieResponse): MovieEntity

}