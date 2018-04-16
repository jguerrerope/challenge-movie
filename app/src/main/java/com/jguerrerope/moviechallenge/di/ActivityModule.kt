package com.jguerrerope.moviechallenge.di

import com.jguerrerope.moviechallenge.ui.MovieListActivity
import com.jguerrerope.moviechallenge.ui.SearchMovieListActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {
    @ContributesAndroidInjector
    internal abstract fun contributeMovieListActivity(): MovieListActivity

    @ContributesAndroidInjector
    internal abstract fun contributeSearchMovieListActivity(): SearchMovieListActivity
}

