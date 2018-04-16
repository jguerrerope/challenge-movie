package com.jguerrerope.moviechallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import com.jguerrerope.moviechallenge.api.MovieResponseMapper
import com.jguerrerope.moviechallenge.api.TMDBService
import com.jguerrerope.moviechallenge.data.Movie
import io.reactivex.Scheduler

/**
 * A simple data source factory which also provides a way to observe the last created data source.
 * This allows us to channel its network request status etc back to the UI. See the Listing creation
 * in the Repository class.
 */
class SearchMovieDataSourceFactory(
        private val webservice: TMDBService,
        private val search: String,
        private val itemsPerPage: Int,
        private val responseMapper: MovieResponseMapper,
        private val backgroundScheduler: Scheduler
) : DataSource.Factory<Int, Movie>() {
    val sourceLiveData = MutableLiveData<SearchMovieDataSource>()
    override fun create(): DataSource<Int, Movie> {
        sourceLiveData.value?.disposables?.clear()
        val source = SearchMovieDataSource(
                webservice, search, itemsPerPage, responseMapper, backgroundScheduler)
        sourceLiveData.postValue(source)
        return source
    }
}
