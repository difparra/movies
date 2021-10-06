package com.diegoparra.movies.data.network

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.diegoparra.movies.data.network.dtos.MovieListItemDto
import timber.log.Timber

private const val MOVIES_STARTING_PAGE_INDEX = 1

class MoviesListPagingSource(
    private val moviesApi: MoviesApi
) : PagingSource<Int, MovieListItemDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieListItemDto> {
        val position = params.key ?: MOVIES_STARTING_PAGE_INDEX
        return try {
            val response = moviesApi.getPopularMovies(page = position)
            val movies = response.results.toMutableList()

            //  In case loadSize is greater than PageSize, additional results should be loaded and appended.
            val totalPagesToLoad = params.loadSize / MoviesApi.PAGE_SIZE
            if (totalPagesToLoad > 1) {
                for(page in (position+1) until (position+totalPagesToLoad)) {
                    if(page <= response.totalPages) {
                        movies.addAll(moviesApi.getPopularMovies(page).results)
                    }
                }
            }

            Timber.d("movies = ${movies.joinToString { "[${it.id}, ${it.title}]" }}")
            val nextKey = if (position == response.totalPages) {
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                position + (params.loadSize / MoviesApi.PAGE_SIZE)
            }
            LoadResult.Page(
                data = movies,
                prevKey = if (position == MOVIES_STARTING_PAGE_INDEX) null else position - 1,
                nextKey = nextKey
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    // The refresh key is used for the initial load of the next PagingSource, after invalidation
    override fun getRefreshKey(state: PagingState<Int, MovieListItemDto>): Int? {
        // We need to get the previous key (or next key if previous is null) of the page
        // that was closest to the most recently accessed index.
        // Anchor position is the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

}