package com.diegoparra.movies.data.local

import com.diegoparra.movies.data.local.entities.MovieEntity
import com.diegoparra.movies.models.Movie

interface MoviesEntityMappers {

    fun toMovie(movieEntity: MovieEntity): Movie

}