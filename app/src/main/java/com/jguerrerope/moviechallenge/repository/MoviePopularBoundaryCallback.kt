package com.jguerrerope.moviechallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PagedList
import android.support.annotation.MainThread
import com.jguerrerope.moviechallenge.api.MovieListResponse
import com.jguerrerope.moviechallenge.api.TMDBService
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.data.NetworkState
import com.jguerrerope.moviechallenge.db.MovieDao
import com.jguerrerope.moviechallenge.extension.logd
import com.jguerrerope.moviechallenge.extension.loge
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.rxkotlin.subscribeBy

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 */
class MoviePopularBoundaryCallback(
        private val itemsPerPage: Int,
        private val webservice: TMDBService,
        private val dao: MovieDao,
        private val handleResponse: (MovieListResponse?) -> Unit,
        private val backgroundScheduler: Scheduler
) : PagedList.BoundaryCallback<Movie>() {

    private var retryCompletable: Completable? = null
    private var reachEnd: Boolean = false

    val networkState = MutableLiveData<NetworkState>()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        networkState.postValue(NetworkState.INITIAL_LOADING)
        Single.fromCallable { (dao.getNextIndex() / itemsPerPage) + 1 }
                .subscribeOn(backgroundScheduler)
                .flatMap { webservice.getMoviePopular(it) }
                .subscribeBy(
                        onSuccess = {
                            logd("onZeroItemsLoaded.onSuccess")
                            /**
                             * every time it gets new items, boundary callback simply inserts them into the database and
                             * paging library takes care of refreshing the list if necessary.
                             */
                            handleResponse.invoke(it)
                            networkState.postValue(NetworkState.LOADED)
                        },
                        onError = {
                            loge("onZeroItemsLoaded.onError", it)
                            retryCompletable = Completable.fromAction { onZeroItemsLoaded() }
                            networkState.postValue(NetworkState.error(it))
                        }
                )
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Movie) {
        if (!reachEnd) {
            networkState.postValue(NetworkState.NEXT_LOADING)
            Single.fromCallable { dao.getNextIndex() }
                    .subscribeOn(backgroundScheduler)
                    .map { (dao.getNextIndex() / itemsPerPage) + 1 }
                    .flatMap { webservice.getMoviePopular(it) }
                    .subscribeBy(
                            onSuccess = {
                                logd("onZeroItemsLoaded.onSuccess")
                                handleResponse.invoke(it)
                                if (it.totalPages == it.page) reachEnd = true
                                networkState.postValue(NetworkState.LOADED)
                            },
                            onError = {
                                loge("onItemAtEndLoaded.onError", it)
                                retryCompletable = Completable.fromAction { onItemAtEndLoaded(itemAtEnd) }
                                networkState.postValue(NetworkState.error(it))
                            }
                    )
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Movie) {
        // ignored, since we only ever append to what's in the DB
    }

    fun retry() = retryCompletable?.observeOn(backgroundScheduler)?.subscribe { }
}