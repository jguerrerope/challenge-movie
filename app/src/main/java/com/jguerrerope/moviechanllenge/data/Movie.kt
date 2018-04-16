package com.jguerrerope.moviechanllenge.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

/**
 * Class that represents a Movie in the app.
 */
@Entity(tableName = "movie")
data class Movie(
        @PrimaryKey
        val id: Int,
        val name: String,
        val popularity: Float,
        val voteCount: Int,
        val voteAverage: Float,
        val overview: String?,
        val backdropPath: String?,
        val posterPath: String?
) : Serializable {
    // To be consistent with changing backend order, we need to keep data like this
    var indexInResponse: Int = -1
}