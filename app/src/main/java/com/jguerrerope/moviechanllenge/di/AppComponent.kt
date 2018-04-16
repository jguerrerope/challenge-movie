package com.jguerrerope.moviechanllenge.di

import com.jguerrerope.moviechanllenge.MovieApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            (AndroidInjectionModule::class),
            (AppModule::class),
            (ActivityModule::class)
        ]
)
abstract class AppComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: MovieApplication): Builder

        fun build(): AppComponent
    }

    abstract fun inject(app: MovieApplication)
}