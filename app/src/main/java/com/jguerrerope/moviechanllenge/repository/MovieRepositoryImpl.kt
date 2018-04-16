package com.jguerrerope.moviechanllenge.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.arch.paging.LivePagedListBuilder
import com.jguerrerope.moviechanllenge.api.MovieListResponse
import com.jguerrerope.moviechanllenge.api.MovieResponseMapper
import com.jguerrerope.moviechanllenge.api.TMDBService
import com.jguerrerope.moviechanllenge.data.Listing
import com.jguerrerope.moviechanllenge.data.Movie
import com.jguerrerope.moviechanllenge.data.NetworkState
import com.jguerrerope.moviechanllenge.db.MovieDatabase
import io.reactivex.Scheduler
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
        private val api: TMDBService,
        private val database: MovieDatabase,
        private val tvShowResponseMapper: MovieResponseMapper
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


    /**
     * Inserts the response into the database while also assigning position indices to items.
     */
    private fun insertMovieListIntoDb(response: MovieListResponse?) {
        response?.let {
            val nextIndex = database.movieDao().getNextIndex()
            val items = tvShowResponseMapper.toEntity(it.results)
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