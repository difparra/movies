package com.diegoparra.movies.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.diegoparra.movies.databinding.FragmentHomeBinding
import com.diegoparra.movies.utils.EventObserver
import com.diegoparra.movies.utils.Resource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val adapter by lazy { MoviesAdapter(viewModel::onMovieClick) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        binding.moviesList.setHasFixedSize(true)
        binding.moviesList.adapter = adapter

        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.movies.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    adapter.submitList(emptyList())
                    binding.errorMessage.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(it.data)
                    binding.errorMessage.isVisible = false
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    adapter.submitList(emptyList())
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = it.failure.message
                }
            }
        }
        viewModel.navigateMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieId = it)
            findNavController().navigate(action)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}