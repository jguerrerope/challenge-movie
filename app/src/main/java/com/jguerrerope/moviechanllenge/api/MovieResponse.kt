package com.jguerrerope.moviechanllenge.api

import com.google.gson.annotations.SerializedName

/**
 * Class to represent the data of a Movie
 */
data class MovieResponse(
        @SerializedName("id")
        val id: Int,

        @SerializedName("original_name")
        val originalName: String,

        @SerializedName("name")
        val name: String,

        @SerializedName("genre_ids")
        val genreIds: List<Int>,

        @SerializedName("popularity")
        val popularity: Float,

        @SerializedName("origin_country")
        val originCountry: List<String>,

        @SerializedName("vote_count")
        val voteCount: Int,

        @SerializedName("vote_average")
        val voteAverage: Float,

        @SerializedName("first_air_date")
        val firstAirDate: String,

        @SerializedName("original_language")
        val originalLanguage: String,

        @SerializedName("overview")
        val overview: String,

        @SerializedName("backdrop_path")
        val backdropPath: String,

        @SerializedName("poster_path")
        val posterPath: String
)