package com.jguerrerope.moviechanllenge.di;

import com.jguerrerope.moviechanllenge.ui.MovieDetailsActivity;
import com.jguerrerope.moviechanllenge.ui.MovieListActivity;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModule {
    @ContributesAndroidInjector
    abstract MovieListActivity contributeMovieListActivity();

    @ContributesAndroidInjector
    abstract MovieDetailsActivity contributeMovieDetailsActivity();
}

