package com.jguerrerope.moviechallenge.di;

import com.jguerrerope.moviechallenge.ui.MovieDetailsActivity;
import com.jguerrerope.moviechallenge.ui.MovieListActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MovieListActivity contributeMovieListActivity();

    @ContributesAndroidInjector
    abstract MovieDetailsActivity contributeMovieDetailsActivity();
}

