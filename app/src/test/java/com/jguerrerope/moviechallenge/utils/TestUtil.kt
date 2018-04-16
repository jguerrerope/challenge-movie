package com.jguerrerope.moviechallenge.utils

import com.jguerrerope.moviechallenge.api.MovieListResponse
import com.jguerrerope.moviechallenge.api.MovieResponse
import com.jguerrerope.moviechallenge.data.Movie

object TestUtil {

    fun createMovieList(size: Int, indexInit: Int = 1): List<Movie> {
        return (0 until size).map {
            val index = indexInit + it
            Movie(
                    id = index,
                    name = "name $index",
                    popularity = index.toFloat(),
                    voteCount = index,
                    voteAverage = index.toFloat(),
                    overview = "overview $index",
                    backdropPath = "backdropPath $index",
                    posterPath = "posterPath $index"
            )
        }
    }

    fun createMovieResponseList(size: Int, indexInit: Int = 1): List<MovieResponse> {
        return (0 until size).map {
            val index = indexInit + it
            MovieResponse(
                    id = index,
                    name = "name $index",
                    originalName = "originalName $index",
                    genreIds = arrayListOf(),
                    originCountry = arrayListOf(),
                    originalLanguage = "originalLanguage $index",
                    popularity = index.toFloat(),
                    voteCount = index,
                    voteAverage = index.toFloat(),
                    overview = "overview $it",
                    firstAirDate = "firstAirDate $index",
                    backdropPath = "backdropPath $index",
                    posterPath = "posterPath $index"
            )
        }
    }


    fun createMovieListResponse(page: Int, size: Int, totalPage: Int): MovieListResponse {
        return MovieListResponse(
                page = page,
                totalResults = totalPage * size,
                totalPages = totalPage,
                results = createMovieResponseList(size)
        )
    }
}