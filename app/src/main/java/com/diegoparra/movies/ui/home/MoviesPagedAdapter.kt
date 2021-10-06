package com.diegoparra.movies.ui.home

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import com.diegoparra.movies.models.Movie

class MoviesPagedAdapter(private val onClickListener: (String) -> Unit) :
    PagingDataAdapter<Movie, MovieViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.create(parent, onClickListener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        movie?.let { holder.bind(it) }
    }
}