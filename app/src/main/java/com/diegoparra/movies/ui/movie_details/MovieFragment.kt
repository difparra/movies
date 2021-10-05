package com.diegoparra.movies.ui.movie_details

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.FloatRange
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.diegoparra.movies.R
import com.diegoparra.movies.databinding.FragmentMovieBinding
import com.diegoparra.movies.models.Genre
import com.diegoparra.movies.models.Movie
import com.diegoparra.movies.utils.Resource
import com.diegoparra.movies.utils.loadImage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.lang.StringBuilder
import java.time.LocalDate

@AndroidEntryPoint
class MovieFragment : Fragment() {

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
            when (it) {
                is Resource.Loading -> {
                    binding.progressBar.isVisible = true
                    binding.errorMessage.isVisible = false
                }
                is Resource.Success -> {
                    binding.progressBar.isVisible = false
                    binding.content.isVisible = true
                    binding.errorMessage.isVisible = false
                    loadMovieData(movie = it.data)
                }
                is Resource.Error -> {
                    binding.progressBar.isVisible = false
                    binding.content.isVisible = false
                    binding.errorMessage.isVisible = true
                    binding.errorMessage.text = it.failure.message
                }
            }
        }
    }

    private fun loadMovieData(movie: Movie) {
        binding.appbarImage.loadImage(movie.backdropUrl)
        binding.collapsingToolbar.title = movie.title
        loadRating(movie.voteAverage)
        loadDetails(movie.releaseDate, movie.runtimeMinutes, movie.genres)
        binding.overview.text = movie.overview
        binding.language.text = movie.language ?: getString(R.string.info_not_available)
        binding.releaseDate.text =
            movie.releaseDate?.toString() ?: getString(R.string.info_not_available)
        loadHomepageUrl(movie.homepageUrl)
    }

    private fun loadRating(@FloatRange(from = 0.0, to = 10.0) voteAverage: Float?) {
        if (voteAverage != null) {
            binding.ratingBar.rating = voteAverage / 2
            binding.ratingText.text = "(${(voteAverage * 10).toInt()}%)"
        } else {
            binding.ratingBar.rating = 0f
            binding.ratingText.text = getString(R.string.info_not_available_short)
        }
    }

    private fun loadDetails(releaseDate: LocalDate?, runtimeMinutes: Int?, genres: List<Genre>) {
        val detailsString = StringBuilder()
        releaseDate?.let {
            detailsString.append(it.year.toString())
        }
        runtimeMinutes?.let {
            val hours = runtimeMinutes / 60
            val minutes = runtimeMinutes % 60
            val str = (if (hours > 0) "${hours}h" else "") + "${minutes}m"
            detailsString.append(" · ").append(str)
        }
        if (genres.isNotEmpty()) {
            detailsString.append(" · ").append(genres.joinToString { it.name })
        }
        binding.details.text = detailsString
    }

    private fun loadHomepageUrl(homepageUrl: String?) {
        if (homepageUrl != null) {
            val spannable = SpannableStringBuilder(homepageUrl).apply {
                setSpan(
                    ForegroundColorSpan(Color.BLUE),
                    0,
                    length,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
                setSpan(UnderlineSpan(), 0, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            binding.homepageUrl.text = spannable
            binding.homepageUrl.setOnClickListener {
                try {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(homepageUrl))
                    startActivity(intent)
                } catch (e: Exception) {
                    Timber.e("Couldn't open homepageUrl: $homepageUrl")
                }
            }
        } else {
            binding.homepageUrl.text = getString(R.string.info_not_available)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}