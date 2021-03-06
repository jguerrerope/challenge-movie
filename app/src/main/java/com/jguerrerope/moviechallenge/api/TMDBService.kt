package com.jguerrerope.moviechallenge.api

import com.jguerrerope.moviechallenge.BuildConfig
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * This interface allows to define the calls that will be used with Retrofit
 */
interface TMDBService {
    @GET("movie/popular?api_key=${BuildConfig.TMDB_ACCESS_TOKEN}")
    fun getMoviePopular(@Query("page") page: Int): Single<MovieListResponse>

    @GET("search/movie?api_key=${BuildConfig.TMDB_ACCESS_TOKEN}")
    fun getSearchMovie(@Query("query") search: String,
                       @Query("page") page: Int): Single<MovieListResponse>
}