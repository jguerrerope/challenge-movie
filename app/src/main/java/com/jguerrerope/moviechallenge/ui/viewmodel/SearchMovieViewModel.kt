package com.jguerrerope.moviechallenge.ui.viewmodel

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.jguerrerope.moviechallenge.Configuration
import com.jguerrerope.moviechallenge.data.Listing
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.extension.logd
import com.jguerrerope.moviechallenge.extension.loge
import com.jguerrerope.moviechallenge.extension.switchMap
import com.jguerrerope.moviechallenge.repository.MovieRepositoryImpl
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SearchMovieViewModel @Inject constructor(
        private val repository: MovieRepositoryImpl
) : ViewModel() {
    private val repoResult = MutableLiveData<Listing<Movie>>()
    private val searchPublishSubject = PublishSubject.create<String>()

    val searchMovies = repoResult.switchMap { it.pagedList }
    val networkState = repoResult.switchMap {
        it.networkState
    }

    init {
        setUpSearchObserver()
    }

    fun retry() = repoResult.value?.retry?.invoke()

    /**
     * Called on every character change made to the search `EditText`
     */
    fun onSearchInputStateChanged(query: String) {
        searchPublishSubject.onNext(query)
    }

    private fun setUpSearchObserver() {
        searchPublishSubject
                .debounce(300, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .map {
                    repository.getSearchMovieListing(it,
                            Configuration.NUMBER_OF_ITEMS_PER_PAGE,
                            Configuration.PREFETCH_DISTANCE, Schedulers.io())
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeBy(
                        onNext = {
                            logd("searchPublishSubject.onSuccess")
                            repoResult.value = it
                        },
                        onError = { loge("searchPublishSubject.onError",it) },
                        onComplete = { logd("searchPublishSubject.onComplete") }
                )
    }
}