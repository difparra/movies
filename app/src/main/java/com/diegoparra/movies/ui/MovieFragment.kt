package com.diegoparra.movies.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.movies.databinding.FragmentMovieBinding
import com.diegoparra.movies.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MovieFragment: Fragment() {

    private var _binding: FragmentMovieBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MovieViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMovieBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.movie.observe(viewLifecycleOwner) {
            when(it) {
                is Resource.Loading -> { /* TODO */ }
                is Resource.Success -> {
                    val movie = it.data
                    binding.text.text = movie.id + "\n" + movie.title
                }
                is Resource.Error ->
                    binding.text.text = it.failure.message
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}