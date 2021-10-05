package com.diegoparra.movies.ui

import androidx.lifecycle.*
import com.diegoparra.movies.data.MoviesRepository
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Resource
import com.diegoparra.movies.utils.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    savedStateHandle: SavedStateHandle
): ViewModel() {

    private val movieId = savedStateHandle.get<String>(MOVIE_ID_SAVED_STATE_KEY)!!

    private val _movie = MutableLiveData<Resource<Movie>>()
    val movie: LiveData<Resource<Movie>> = _movie

    init {
        viewModelScope.launch {
            _movie.value = Resource.Loading
            _movie.value = moviesRepository.getMovieById(movieId).toResource()
        }
    }


    companion object {
        const val MOVIE_ID_SAVED_STATE_KEY = "movie_id"
    }


}