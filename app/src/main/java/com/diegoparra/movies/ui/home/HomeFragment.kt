package com.diegoparra.movies.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.distinctUntilChanged
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.map
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import com.diegoparra.movies.R
import com.diegoparra.movies.databinding.FragmentHomeBinding
import com.diegoparra.movies.utils.EventObserver
import com.diegoparra.movies.utils.runIfTrue
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.net.UnknownHostException

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by viewModels()
    private val pagedAdapter by lazy { MoviesPagedAdapter(viewModel::onMovieClick) }
    private val pagedAdapterWithLoadStateHeaderAndFooter by lazy {
        pagedAdapter.withLoadStateHeaderAndFooter(
            header = MoviesLoadStateAdapter { pagedAdapter.retry() },
            footer = MoviesLoadStateAdapter { pagedAdapter.retry() }
        )
    }
    private val listAdapter by lazy { MoviesListAdapter(viewModel::onMovieClick) }

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
        setUpMoviesPagedAdapter()
        setUpSearchView()
        subscribeUi()
    }

    private fun setUpMoviesPagedAdapter() {
        binding.retryButton.setOnClickListener { pagedAdapter.retry() }

        viewLifecycleOwner.lifecycleScope.launch {
            pagedAdapter.loadStateFlow.collectLatest { loadState ->
                Timber.d("loadState = $loadState")

                //  Loading state
                binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

                //  Empty list
                val isListEmpty =
                    loadState.refresh is LoadState.NotLoading && pagedAdapter.itemCount == 0
                binding.moviesList.isVisible = !isListEmpty
                isListEmpty.runIfTrue { binding.errorMessage.text = getString(R.string.empty_list) }

                //  If initial load or refresh fails
                val initialFailure = loadState.source.refresh as? LoadState.Error
                    ?: loadState.refresh as? LoadState.Error
                initialFailure?.error?.let { binding.errorMessage.text = getMessageFromFailure(it) }
                binding.retryButton.isVisible = initialFailure != null

                //  Error message visibility
                binding.errorMessage.isVisible = isListEmpty || initialFailure != null

            }
        }
    }

    private fun getMessageFromFailure(exception: Throwable): String {
        Timber.e(exception)
        return if (exception is UnknownHostException) {
            getString(R.string.failure_network_connection)
        } else {
            exception.message ?: getString(R.string.failure_generic_message)
        }
    }

    private fun setUpSearchView() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                loadResults(p0)
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                Timber.d("query = $p0")
                loadResults(p0)
                return true
            }

            private fun loadResults(str: String?) {
                str?.let { viewModel.setQuery(it) }
            }
        })
    }

    private fun subscribeUi() {
        viewModel.setVisibleListFn { pagedAdapter.snapshot().items }
        viewModel.moviesList.map { it is MoviesListState.MoviesPagedList }.distinctUntilChanged()
            .observe(viewLifecycleOwner) { isPagedAdapter ->
                binding.moviesList.adapter =
                    if (isPagedAdapter) pagedAdapterWithLoadStateHeaderAndFooter else listAdapter
            }
        viewModel.moviesList.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it is MoviesListState.Loading
            binding.errorMessage.isVisible = it is MoviesListState.NoSearchResults
            when(it) {
                is MoviesListState.MoviesPagedList -> {
                    it.data.asLiveData().observe(viewLifecycleOwner) {
                        pagedAdapter.submitData(lifecycle, it)
                    }
                }
                is MoviesListState.SearchSuccess -> {
                    listAdapter.submitList(it.data)
                }
                is MoviesListState.NoSearchResults -> {
                    listAdapter.submitList(emptyList())
                    binding.errorMessage.text = getString(R.string.empty_list)
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