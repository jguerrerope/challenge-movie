package com.jguerrerope.moviechallenge.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import com.jguerrerope.moviechallenge.api.MovieListResponse
import com.jguerrerope.moviechallenge.api.MovieResponseMapper
import com.jguerrerope.moviechallenge.api.TMDBService
import com.jguerrerope.moviechallenge.data.Listing
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.data.NetworkState
import com.jguerrerope.moviechallenge.db.MovieDatabase
import com.jguerrerope.moviechallenge.extension.switchMap
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
        private val api: TMDBService,
        private val database: MovieDatabase,
        private val responseMapper: MovieResponseMapper
) : MovieRepository {

    override fun getMoviePopularListing(
            itemsPerPage: Int,
            backgroundScheduler: Scheduler): Listing<Movie> {
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = MoviePopularBoundaryCallback(
                webservice = api,
                dao = database.movieDao(),
                itemsPerPage = itemsPerPage,
                handleResponse = this::insertMovieListIntoDb,
                backgroundScheduler = backgroundScheduler
        )
        // create a data source factory from Room
        val builder =
                LivePagedListBuilder(database.movieDao().movieDataFactory(), itemsPerPage)
                        .setBoundaryCallback(boundaryCallback)

        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger, {
            refreshMoviePopular(backgroundScheduler)
        })

        return Listing(
                pagedList = builder.build(),
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.retry()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }

    override fun getSearchMovieListing(
            search: String,
            itemsPerPage: Int,
            prefetchDistance: Int,
            backgroundScheduler: Scheduler): Listing<Movie> {

        val sourceFactory = SearchMovieDataSourceFactory(api, search, itemsPerPage,
                responseMapper,
                backgroundScheduler)
        val pagedListConfig = PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setInitialLoadSizeHint(prefetchDistance)
                .setPageSize(itemsPerPage)
                .build()
        val pagedList = LivePagedListBuilder(sourceFactory, pagedListConfig)
                .build()
        val networkState = sourceFactory.sourceLiveData.switchMap { it.networkState }
        return Listing(
                pagedList = pagedList,
                networkState = networkState,
                retry = { sourceFactory.sourceLiveData.value?.retry() },
                refresh = {},
                refreshState = networkState
        )
    }

    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertMovieListIntoDb(response: MovieListResponse?) {
        response?.let {
            val nextIndex = database.movieDao().getNextIndex()
            val items = responseMapper.toEntity(it.results)
            items.forEachIndexed { index, item -> item.indexInResponse = nextIndex + index }
            database.movieDao().insertList(items)
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives,
     * clear the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    private fun refreshMoviePopular(backgroundScheduler: Scheduler): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        api.getMoviePopular(1)
                .subscribeOn(backgroundScheduler)
                .subscribeBy(
                        onSuccess = {
                            database.runInTransaction {
                                database.movieDao().deleteAll()
                                insertMovieListIntoDb(it)
                            }

                            // since we are in bg thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)
                        },
                        onError = {
                            // retrofit calls this on main thread so safe to call set value
                            networkState.postValue(NetworkState.error(it))
                        }
                )
        return networkState
    }
}