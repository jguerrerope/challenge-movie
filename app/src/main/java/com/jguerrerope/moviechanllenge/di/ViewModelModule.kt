package com.jguerrerope.moviechanllenge.di

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.jguerrerope.moviechanllenge.ui.viewmodel.MoviePopularViewModel
import com.jguerrerope.moviechanllenge.ui.viewmodel.MovieViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(MoviePopularViewModel::class)
    abstract fun bindMoviePopularViewModel(viewModel: MoviePopularViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(factory: MovieViewModelFactory): ViewModelProvider.Factory
}