package com.diegoparra.movies.ui.home

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.diegoparra.movies.models.Movie

class MoviesListAdapter(private val onClickListener: (String) -> Unit) :
    ListAdapter<Movie, MovieViewHolder>(MovieDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.create(parent, onClickListener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }
}