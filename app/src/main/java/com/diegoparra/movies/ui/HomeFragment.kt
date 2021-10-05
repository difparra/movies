package com.diegoparra.movies.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        subscribeUi()
        binding.text.setOnClickListener {
            //  TODO: set correct movieId
            viewModel.onMovieClick("588228")
        }
    }

    private fun subscribeUi() {
        viewModel.movies.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> { /* TODO */
                }
                is Resource.Success ->
                    binding.text.text = it.data.joinToString("\n") { it.title }
                is Resource.Error ->
                    binding.text.text = it.failure.message
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