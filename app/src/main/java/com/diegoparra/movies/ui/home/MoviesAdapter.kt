package com.diegoparra.movies.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.movies.databinding.ListItemMovieBinding
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.loadImage

class MoviesAdapter(private val onClickListener: (String) -> Unit) :
    ListAdapter<Movie, MoviesAdapter.MovieViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.from(parent, onClickListener)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movie = getItem(position)
        holder.bind(movie)
    }


    class MovieViewHolder(
        private val binding: ListItemMovieBinding,
        private val onClickListener: (String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        private lateinit var movie: Movie

        init {
            binding.root.setOnClickListener { onClickListener(movie.id) }
        }

        companion object {
            fun from(parent: ViewGroup, onClickListener: (String) -> Unit): MovieViewHolder {
                return MovieViewHolder(
                    ListItemMovieBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    onClickListener
                )
            }
        }

        fun bind(movie: Movie) {
            this.movie = movie
            binding.image.loadImage(movie.posterUrl)
            binding.title.text = movie.title
        }

    }

    companion object DiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }

    }
}