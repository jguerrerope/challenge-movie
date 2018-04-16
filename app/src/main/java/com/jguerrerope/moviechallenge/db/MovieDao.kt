package com.jguerrerope.moviechallenge.db

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.jguerrerope.moviechallenge.data.Movie

/**
 * Data access object to operate over MovieEntity
 */
@Dao
interface MovieDao {

    /**
     * Inserts a list of Movie in the database. If some of those conflict, we assume that new info has just
     * arrived, so we replace it.
     *
     * @param movieList The list of Movie
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertList(movieList: List<Movie>)

    /**
     * Gets a [DataSource.Factory] of [Movie] that it automatically handles how to provide specific items to the UI.
     *
     * @return A reactive [Movie] data source factory
     */
    @Query("SELECT * FROM movie ORDER BY indexInResponse ASC")
    fun movieDataFactory(): DataSource.Factory<Int, Movie>

    /**
     * Gets the next index that can be used to insert in the [Movie] table in the database
     *
     * @return The next index to use in [Movie] table
     */
    @Query("SELECT MAX(indexInResponse) + 1 FROM movie")
    fun getNextIndex(): Int

    /**
     * Delete all movie in database
     */
    @Query("delete FROM movie")
    fun deleteAll()
}