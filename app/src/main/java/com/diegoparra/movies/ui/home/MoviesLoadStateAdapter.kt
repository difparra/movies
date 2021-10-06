package com.diegoparra.movies.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.diegoparra.movies.R
import com.diegoparra.movies.databinding.LoadStateFooterMovieListBinding
import java.net.UnknownHostException

class MoviesLoadStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MoviesLoadStateAdapter.MoviesLoadStateViewHolder>() {

    override fun onBindViewHolder(holder: MoviesLoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): MoviesLoadStateViewHolder {
        return MoviesLoadStateViewHolder.create(parent, retry)
    }


    class MoviesLoadStateViewHolder(
        private val binding: LoadStateFooterMovieListBinding,
        retry: () -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.retryButton.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                binding.errorMsg.text = getMessageFromFailure(loadState.error)
            }
            binding.progressBar.isVisible = loadState is LoadState.Loading
            binding.retryButton.isVisible = loadState is LoadState.Error
            binding.errorMsg.isVisible = loadState is LoadState.Error
        }

        private fun getMessageFromFailure(exception: Throwable): String {
            val context = binding.root.context
            return if(exception is UnknownHostException) {
                context.getString(R.string.failure_network_connection)
            } else {
                exception.message ?: context.getString(R.string.failure_generic_message)
            }
        }

        companion object {
            fun create(parent: ViewGroup, retry: () -> Unit): MoviesLoadStateViewHolder {
                return MoviesLoadStateViewHolder(
                    LoadStateFooterMovieListBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    ),
                    retry
                )
            }
        }
    }
}