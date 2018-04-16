package com.jguerrerope.moviechanllenge.repository

import android.arch.lifecycle.MutableLiveData
import android.arch.paging.DataSource
import android.arch.paging.PositionalDataSource
import com.jguerrerope.moviechanllenge.data.Movie


/**
 * Fake class to simulate Room expected behaviour
 */
class FakeRoomDataSourceFactory(var items: List<Movie>) : DataSource.Factory<Int, Movie>() {
    val sourceLiveData = MutableLiveData<FakeDataSource>()

    override fun create(): DataSource<Int, Movie> {
        val source = FakeDataSource(items)
        sourceLiveData.postValue(source)
        return source
    }

    class FakeDataSource(var items: List<Movie>) : PositionalDataSource<Movie>() {
        override fun loadInitial(params: PositionalDataSource.LoadInitialParams,
                                 callback: PositionalDataSource.LoadInitialCallback<Movie>) {
            val totalCount = items.size

            val position = PositionalDataSource.computeInitialLoadPosition(params, totalCount)
            val loadSize = PositionalDataSource.computeInitialLoadSize(params, position, totalCount)

            // for simplicity, we could return everything immediately,
            // but we tile here since it's expected behavior
            val sublist = items.subList(position, position + loadSize)
            callback.onResult(sublist, position, totalCount)
        }

        override fun loadRange(params: PositionalDataSource.LoadRangeParams,
                               callback: PositionalDataSource.LoadRangeCallback<Movie>) {
            callback.onResult(items.subList(params.startPosition,
                    params.startPosition + params.loadSize))
        }
    }
}