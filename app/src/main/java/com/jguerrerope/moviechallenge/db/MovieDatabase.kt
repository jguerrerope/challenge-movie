package com.jguerrerope.moviechallenge.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.jguerrerope.moviechallenge.data.Movie

/**
 * Main database description.
 */
@Database(entities = [(Movie::class)], version = 1,exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
