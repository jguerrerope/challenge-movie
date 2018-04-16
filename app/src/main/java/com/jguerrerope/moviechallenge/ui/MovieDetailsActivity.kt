package com.jguerrerope.moviechallenge.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jguerrerope.moviechallenge.R
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.utils.TMDBImageUtils
import kotlinx.android.synthetic.main.activity_movie_details.*
import kotlinx.android.synthetic.main.activity_movie_details_content.*

class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.transition_enter_right, R.anim.transition_no_animation)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
        movie = intent?.extras?.getSerializable(EXTRA_MOVIE) as Movie?
                ?: throw RuntimeException("bad initialization. not found some extras")
        setUpViews()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // handle item selection
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.transition_no_animation, R.anim.transition_exit_right)
    }

    private fun setUpViews() {
        setUpToolbar()
        bindMovie(movie)
    }

    private fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = movie.title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.back_white)
        collapsingToolbar.title = movie.title
    }

    private fun bindMovie(movie: Movie) {
        movieOverview.text = movie.overview ?: ""
        movieVoteAverage.text = movie.voteAverage.toString()
        movieVoteCount.text = movie.voteCount.toString()
        moviePopularity.text = getString(R.string.popularity_format, movie.popularity)

        val options = RequestOptions()
                .placeholder(R.drawable.tv_place_holder)
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        val url = TMDBImageUtils.formatUrlImageWithW500(movie.backdropPath ?: movie.posterPath
        ?: "")
        Glide
                .with(this)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .apply(options)
                .into(tvShowImageView)
    }

    companion object {
        const val EXTRA_MOVIE = "EXTRA_MOVIE"
    }
}
