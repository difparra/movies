package com.diegoparra.movies.ui.home

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.diegoparra.movies.data.MoviesRepository
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Event
import com.diegoparra.movies.utils.Resource
import com.diegoparra.movies.utils.toResource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository
) : ViewModel() {

    val movies = moviesRepository
        .getPopularMoviesStream()
        .cachedIn(viewModelScope)
        .asLiveData()

    private val _navigateMovieDetails = MutableLiveData<Event<String>>()
    val navigateMovieDetails: LiveData<Event<String>> = _navigateMovieDetails

    fun onMovieClick(movieId: String) {
        _navigateMovieDetails.value = Event(movieId)
    }

}