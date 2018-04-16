package com.jguerrerope.moviechanllenge.repository

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import android.arch.paging.PagedList
import com.jguerrerope.moviechanllenge.api.MovieResponseMapper
import com.jguerrerope.moviechanllenge.api.TMDBService
import com.jguerrerope.moviechanllenge.data.Listing
import com.jguerrerope.moviechanllenge.data.Movie
import com.jguerrerope.moviechanllenge.data.NetworkState
import com.jguerrerope.moviechanllenge.db.MovieDao
import com.jguerrerope.moviechanllenge.utils.TestUtil
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.stub
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class MovieRepositoryTest {
    @Suppress("unused")
    @get:Rule // used to make all live data calls sync
    val instantExecutor = InstantTaskExecutorRule()

    private val movieResponseMapper = MovieResponseMapper()

    /**
     * asserts that empty list works fine
     */
    @Test
    fun emptyList() {
        val mockMovieDao = mock<MovieDao> {
            on { movieDataFactory() } doReturn FakeRoomDataSourceFactory(arrayListOf())
            on { getNextIndex() } doReturn 1
        }

        val repository = MovieRepositoryImpl(
                mock {},
                mock { on { movieDao() } doReturn mockMovieDao },
                movieResponseMapper
        )

        val listing = repository.getMoviePopularListing(
                5, Schedulers.trampoline())
        val pagedList = getPagedList(listing)
        MatcherAssert.assertThat(pagedList.size, CoreMatchers.`is`(0))
    }

    /**
     * asserts loading a full list in multiple pages
     */
    @Test
    fun verifyCompleteList() {
        val itemsOnePage = TestUtil.createMovieList(5)
        val mockDao = mock<MovieDao> {
            on { movieDataFactory() } doReturn FakeRoomDataSourceFactory(itemsOnePage)
            on { getNextIndex() } doReturn 1
        }

        val repository = MovieRepositoryImpl(
                mock {},
                mock { on { movieDao() } doReturn mockDao },
                movieResponseMapper
        )

        val listing = repository.getMoviePopularListing(5, Schedulers.trampoline())
        val pagedList = getPagedList(listing)

        // trigger loading of the whole list
        pagedList.loadAround(itemsOnePage.size - 1)
        assertThat(pagedList, CoreMatchers.`is`(itemsOnePage))

        val itemsTwoPage = TestUtil.createMovieList(10)
        mockDao.stub {
            on { movieDataFactory() } doReturn FakeRoomDataSourceFactory(itemsTwoPage)
        }

        // trigger loading of the whole list
        pagedList.loadAround(itemsTwoPage.size - 1)
        assertThat(pagedList, CoreMatchers.`is`(itemsOnePage))

    }

    /**
     * asserts the retry logic when initial load request fails
     */
    @Test
    fun retryInInitialLoad() {
        val fakeRoomDataSource = FakeRoomDataSourceFactory(arrayListOf())
        val mockApi = mock<TMDBService> {
            on { getMoviePopular(1) } doReturn Single.error(RuntimeException("error"))
        }
        val mockDao = mock<MovieDao> {
            on { movieDataFactory() } doReturn fakeRoomDataSource
            on { getNextIndex() } doReturn 1
        }
        val repository = MovieRepositoryImpl(
                mockApi,
                mock { on { movieDao() } doReturn mockDao },
                movieResponseMapper
        )

        val listing = repository.getMoviePopularListing(5, Schedulers.trampoline())

        assertThat(getPagedList(listing).size, CoreMatchers.`is`(0))

        @Suppress("UNCHECKED_CAST")
        val networkObserver = Mockito.mock(Observer::class.java) as Observer<NetworkState>
        listing.networkState.observeForever(networkObserver)

        mockApi.stub {
            on { getMoviePopular(1) } doReturn
                    Single.just(TestUtil.createMovieListResponse(page = 1, size = 5, totalPage = 3))
        }

        listing.retry()
        fakeRoomDataSource.items = TestUtil.createMovieList(5)
        fakeRoomDataSource.sourceLiveData.value?.invalidate()

        assertThat(getPagedList(listing).size, CoreMatchers.`is`(5))
        assertThat(getNetworkState(listing), CoreMatchers.`is`(NetworkState.LOADED))

        val inOrder = Mockito.inOrder(networkObserver)
        inOrder.verify(networkObserver).onChanged(NetworkState.error("error"))
        inOrder.verify(networkObserver).onChanged(NetworkState.INITIAL_LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verify(networkObserver).onChanged(NetworkState.NEXT_LOADING)
        inOrder.verify(networkObserver).onChanged(NetworkState.LOADED)
        inOrder.verifyNoMoreInteractions()
    }


    /**
     * extract the latest paged list from the listing
     */
    private fun getPagedList(listing: Listing<Movie>): PagedList<Movie> {
        val observer = LoggingObserver<PagedList<Movie>>()
        listing.pagedList.observeForever(observer)
        MatcherAssert.assertThat(observer.value, CoreMatchers.`is`(CoreMatchers.notNullValue()))
        return observer.value!!
    }

    /**
     * extract the latest network state from the listing
     */
    private fun getNetworkState(listing: Listing<Movie>): NetworkState? {
        val networkObserver = LoggingObserver<NetworkState>()
        listing.networkState.observeForever(networkObserver)
        return networkObserver.value
    }

    /**
     * simple observer that logs the latest value it receives
     */
    private class LoggingObserver<T> : Observer<T> {
        var value: T? = null
        override fun onChanged(t: T?) {
            this.value = t
        }
    }
}