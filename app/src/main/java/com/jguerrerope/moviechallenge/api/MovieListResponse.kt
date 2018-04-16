package com.jguerrerope.moviechallenge.api

import com.google.gson.annotations.SerializedName

data class MovieListResponse(
        @SerializedName("page")
        val page: Int,

        @SerializedName("total_results")
        val totalResults: Int,

        @SerializedName("total_pages")
        val totalPages: Int,

        @SerializedName("results")
        val results: List<MovieResponse>
)