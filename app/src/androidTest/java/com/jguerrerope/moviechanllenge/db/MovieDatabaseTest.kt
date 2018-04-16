package com.jguerrerope.moviechanllenge.db

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import com.jguerrerope.moviechanllenge.Configuration
import com.jguerrerope.moviechanllenge.data.Movie
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MovieDatabaseTest {
    companion object {
        private val testTvShowList = (1..Configuration.NUMBER_OF_ITEMS_PER_PAGE).map {
            Movie(
                    id = it,
                    name = "name $it",
                    popularity = it.toFloat(),
                    voteCount = it,
                    voteAverage = it.toFloat(),
                    overview = "overview $it",
                    backdropPath = "backdropPath $it",
                    posterPath = "posterPath $it"
            ).apply {
                indexInResponse = it
            }
        }
    }

    private lateinit var db: MovieDatabase

    @Before
    fun initDb() {
        db = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(),
                MovieDatabase::class.java).build()
    }

    @After
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(InterruptedException::class)
    fun insertAndRead() {
        db.movieDao().insertList(testTvShowList)

        val index = db.movieDao().getNextIndex()
        assertThat(index, CoreMatchers.`is`(Configuration.NUMBER_OF_ITEMS_PER_PAGE + 1))
    }
}