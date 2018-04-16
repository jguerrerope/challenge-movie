package com.jguerrerope.moviechallenge.ui

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.jguerrerope.moviechallenge.R
import com.jguerrerope.moviechallenge.di.Injectable

class MovieDetailsActivity : AppCompatActivity(), Injectable {
    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(R.anim.transition_fade_in, R.anim.transition_no_animation)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)
    }
}