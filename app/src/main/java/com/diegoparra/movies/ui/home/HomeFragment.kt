package com.diegoparra.movies.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.diegoparra.movies.R
import com.diegoparra.movies.databinding.FragmentHomeBinding
import com.diegoparra.movies.utils.EventObserver
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException

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
        binding.moviesList.adapter = adapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { adapter.retry() },
            footer = MoviesLoadStateAdapter { adapter.retry() }
        )
        binding.retryButton.setOnClickListener { adapter.retry() }
        loadStatesMoviesList()
        subscribeUi()
    }

    private fun subscribeUi() {
        viewModel.movies.observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
        viewModel.navigateMovieDetails.observe(viewLifecycleOwner, EventObserver {
            val action = HomeFragmentDirections.actionHomeFragmentToMovieFragment(movieId = it)
            findNavController().navigate(action)
        })
    }

    private fun loadStatesMoviesList() {
        lifecycleScope.launch {
            adapter.loadStateFlow.collect { loadState ->

                //  Loading state
                binding.progressBar.isVisible = loadState.source.refresh is LoadState.Loading

                //  Empty list
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0
                binding.moviesList.isVisible = !isListEmpty
                if (isListEmpty) {
                    binding.errorMessage.text = getString(R.string.empty_list)
                }

                //  If initial load or refresh fails
                val initialFailure = loadState.source.refresh as? LoadState.Error
                    ?: loadState.refresh as? LoadState.Error
                initialFailure?.error?.let {
                    binding.errorMessage.text = getMessageFromFailure(it)
                }
                binding.retryButton.isVisible = initialFailure != null

                //  Error message visibility
                binding.errorMessage.isVisible = isListEmpty || initialFailure != null


                //  Pop up error at the time some append or prepend fails. The error will also be
                //  shown with the adapter, but pop up message helps to have an idea on when it happened.
                val errorState = loadState.source.append as? LoadState.Error
                    ?: loadState.source.prepend as? LoadState.Error
                    ?: loadState.append as? LoadState.Error
                    ?: loadState.prepend as? LoadState.Error
                errorState?.let {
                    Snackbar.make(binding.root, getMessageFromFailure(it.error), Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getMessageFromFailure(exception: Throwable): String {
        Timber.e(exception)
        return if(exception is UnknownHostException) {
            getString(R.string.failure_network_connection)
        } else {
            exception.message ?: getString(R.string.failure_generic_message)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}