package com.jguerrerope.moviechallenge.ui.item

import android.content.Context
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.jguerrerope.moviechallenge.R
import com.jguerrerope.moviechallenge.data.Movie
import com.jguerrerope.moviechallenge.utils.TMDBImageUtils
import kotlinx.android.synthetic.main.view_item_movie.view.*

class MovieItemView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {
    init {
        LayoutInflater.from(context).inflate(R.layout.view_item_movie, this, true)
        useCompatPadding = true
        radius = 10f
        cardElevation = 4f
    }

    /**
     * Bind a Movie to our view. So if we were placeholding this items we stop that behaviour and set the view
     * with proper information.
     *
     * @param movie The item with the information to be shown
     */
    fun bind(movie: Movie) {
        movieTitle.text = movie.title
        movieVoteAverage.text = movie.voteAverage.toString()

        val options = RequestOptions()
                .placeholder(R.drawable.tv_place_holder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.DATA)

        val url = TMDBImageUtils.formatUrlImageWithW500(
                movie.backdropPath ?: movie.posterPath ?: "")
        Glide
                .with(context)
                .load(url)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .apply(options)
                .into(movieImageView)
    }
}