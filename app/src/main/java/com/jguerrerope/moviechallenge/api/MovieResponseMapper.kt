package com.jguerrerope.moviechallenge.api

import com.jguerrerope.moviechallenge.data.Movie
import javax.inject.Inject

class MovieResponseMapper @Inject constructor() {

    fun toEntity(value: MovieResponse): Movie {
        return Movie(
                id = value.id,
                title = value.title,
                popularity = value.popularity,
                voteCount = value.voteCount,
                voteAverage = value.voteAverage,
                overview = value.overview,
                backdropPath = value.backdropPath,
                posterPath = value.posterPath
        )
    }

    fun toEntity(values: List<MovieResponse>) = values.map { toEntity(it) }
}
