package com.diegoparra.movies.data.network.dtos

import com.google.gson.annotations.SerializedName

data class MoviesListResponse(
    val page: Int,
    val results: List<MovieListItemDto>,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("total_results") val totalResults: Int
)