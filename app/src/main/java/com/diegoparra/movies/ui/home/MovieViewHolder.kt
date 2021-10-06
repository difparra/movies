package com.diegoparra.movies.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.movies.databinding.ListItemMovieBinding
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.loadImage

class MovieViewHolder(
    private val binding: ListItemMovieBinding,
    private val onClickListener: (String) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    private var movie: Movie? = null

    init {
        binding.root.setOnClickListener { movie?.let { onClickListener(it.id) } }
    }

    fun bind(movie: Movie) {
        this.movie = movie
        binding.image.loadImage(movie.posterUrl)
        binding.title.text = movie.title
    }

    companion object {
        fun create(parent: ViewGroup, onClickListener: (String) -> Unit): MovieViewHolder {
            return MovieViewHolder(
                ListItemMovieBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onClickListener
            )
        }
    }

}