package com.jguerrerope.moviechallenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.PositionalDataSource
import com.jguerrerope.moviechallenge.api.MovieResponseMapper
import com.jguerrerope.moviechallenge.api.TMDBService
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.data.NetworkState
import com.jguerrerope.moviechallenge.extension.logd
import com.jguerrerope.moviechallenge.extension.loge
import io.reactivex.Completable
import io.reactivex.Scheduler
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.rxkotlin.subscribeBy

class SearchMovieDataSource(
        private val webservice: TMDBService,
        private val search: String,
        private val itemsPerPage: Int,
        private val responseMapper: MovieResponseMapper,
        private val backgroundScheduler: Scheduler
) : PositionalDataSource<Movie>() {

    /**
     * There is no sync on the state because paging will always call loadInitial first then wait
     * for it to return some success value before calling loadAfter.
     */
    val networkState = MutableLiveData<NetworkState>()
    var disposables = CompositeDisposable()
    private var retryCompletable: Completable? = null
    private var reachEnd: Boolean = false

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Movie>) {
        networkState.postValue(NetworkState.LOADING)
        disposables += webservice.getSearchMovie(search, 1)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            logd("loadInitial.onSuccess")
                            if (it.totalPages == it.page) reachEnd = true
                            networkState.postValue(NetworkState.LOADED)
                            callback.onResult(responseMapper.toEntity(it.results), 0, it.results.size)
                        },
                        onError = {
                            loge("loadInitial.onError", it)
                            networkState.postValue(NetworkState.error(it))
                            retryCompletable = Completable.fromAction {
                                loadInitial(params, callback)
                            }
                        }
                )
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Movie>) {
        val nextPage = (params.startPosition / itemsPerPage) + 1
        networkState.postValue(NetworkState.LOADING)
        disposables += webservice.getSearchMovie(search, nextPage)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            logd("loadInitial.onSuccess")
                            if (it.totalPages == it.page) reachEnd = true
                            networkState.postValue(NetworkState.LOADED)
                            callback.onResult(responseMapper.toEntity(it.results))
                        },
                        onError = {
                            loge("loadInitial.onError", it)
                            networkState.postValue(NetworkState.error(it))
                            retryCompletable = Completable.fromAction {
                                loadRange(params, callback)
                            }
                        }
                )
    }

    fun retry() = retryCompletable?.observeOn(backgroundScheduler)?.subscribe { }
}