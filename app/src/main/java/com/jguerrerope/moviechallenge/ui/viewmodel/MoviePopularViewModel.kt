package com.jguerrerope.moviechallenge.ui.viewmodel

import android.arch.lifecycle.ViewModel
import com.jguerrerope.moviechallenge.Configuration
import com.jguerrerope.moviechallenge.extension.switchMap
import com.jguerrerope.moviechallenge.repository.MovieRepositoryImpl
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MoviePopularViewModel @Inject constructor(
        repository: MovieRepositoryImpl
) : ViewModel() {
    private val repoResult = AbsentLiveData.create(
            repository.getMoviePopularListing(
                    Configuration.NUMBER_OF_ITEMS_PER_PAGE, Schedulers.io())
    )

    val moviesPopular = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap { it.networkState }
    val refreshState = repoResult.switchMap { it.refreshState }

    fun retry() = repoResult.value?.retry?.invoke()

    fun refresh() = repoResult.value?.refresh?.invoke()
}