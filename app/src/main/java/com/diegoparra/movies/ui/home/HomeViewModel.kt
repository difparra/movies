package com.diegoparra.movies.ui.home

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.diegoparra.movies.data.MoviesRepository
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private var getVisibleListFn: () -> List<Movie> = { emptyList() }
    fun setVisibleListFn(fn: ()->List<Movie>) {
        getVisibleListFn = fn
    }


    private val _query =
        savedStateHandle.getLiveData<String>(QUERY_SAVED_STATE_KEY, initialQuery).asFlow()

    fun setQuery(query: String) {
        savedStateHandle.set(QUERY_SAVED_STATE_KEY, query)
    }

    private val popularMoviesPagedList = moviesRepository
        .getPopularMoviesStream()
        .cachedIn(viewModelScope)

    private val _moviesList = MutableStateFlow<MoviesListState>(MoviesListState.Loading)
    val moviesList = _moviesList.asLiveData()

    init {
        viewModelScope.launch {
            _query.collect { query ->
                _moviesList.value = MoviesListState.Loading
                if (query.isEmpty()) {
                    _moviesList.value = MoviesListState.MoviesPagedList(popularMoviesPagedList)
                } else {
                    val searchResults = getVisibleListFn.invoke()
                        .filter { it.title.contains(query, ignoreCase = true) }
                    if(searchResults.isEmpty()) {
                        _moviesList.value = MoviesListState.NoSearchResults
                    }else {
                        _moviesList.value = MoviesListState.SearchSuccess(searchResults)
                    }
                }
            }
        }
    }


    private val _navigateMovieDetails = MutableLiveData<Event<String>>()
    val navigateMovieDetails: LiveData<Event<String>> = _navigateMovieDetails

    fun onMovieClick(movieId: String) {
        _navigateMovieDetails.value = Event(movieId)
    }

    companion object {
        const val QUERY_SAVED_STATE_KEY = "query"
        const val initialQuery = ""
    }

}

sealed class MoviesListState {
    object Loading : MoviesListState()
    class MoviesPagedList(val data: Flow<PagingData<Movie>>) : MoviesListState()
    object NoSearchResults : MoviesListState()
    class SearchSuccess(val data: List<Movie>) : MoviesListState()
    class Failure(val failure: Exception) : MoviesListState()
}


/*
    Another option I tried but didn't really worked:
        Option 2: This may be a good option, but just in specific cases as it has some limitations:
        - Can't control search states properly, as neither:
            - LoadStateFlow of adapter won't emit unless pagingSource / api is requested. That means
            I won't get an empty list in loadStateFlow, and in addition I can't know when the filtering
            began and ended this way.
            - Other option may be adding some external variables inside the viewModel, however, I can
            know when filtering began but not when ended, so I can't check for loading state nor empty state.
        - pagingSource will collect more pages until filtered results are enough to fill the screen and
        not call the next page or there are not more pages left to query, but data may increase too much
        between queries and movies.filter will be slow. For example, if I put some query: "the suicid"
        it will get one result from the first pages, and then continue loading pages to get
        a minimum of results to show or until the totalPages (500) in the API have been queried.
*/
/*
val movies = moviesRepository
    .getPopularMoviesStream()
    .cachedIn(viewModelScope)
    .combine(_query) { movies, query ->
        Timber.d("combine called!, query = $query, movies = $movies")
        if (query.isEmpty()) {
            movies
        } else {
            movies.filter { it.title.contains(query, ignoreCase = true) }
        }
    }
    .flowOn(Dispatchers.Default)
    .cachedIn(viewModelScope)
    .asLiveData()
    */