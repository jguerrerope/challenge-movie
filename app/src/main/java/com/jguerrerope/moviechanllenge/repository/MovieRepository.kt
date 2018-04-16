package com.jguerrerope.moviechanllenge.repository

import com.jguerrerope.moviechanllenge.data.Listing
import com.jguerrerope.moviechanllenge.data.Movie
import io.reactivex.Scheduler

/**
 * Repository to handle which data source must be used to provide all repo related information
 */
interface MovieRepository {
    /**
     * Gets a [Listing] of Movie
     *
     * @param itemsPerPage The number of items that we want to retrieve
     * @param backgroundScheduler The scheduler of background processing
     * @return [Listing]  a Listing for the given Movie popular.
     */
    fun getMoviePopularListing(itemsPerPage: Int, backgroundScheduler: Scheduler): Listing<Movie>
}