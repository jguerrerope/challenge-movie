package com.jguerrerope.moviechanllenge.di

import android.arch.persistence.room.Room
import com.jguerrerope.moviechanllenge.Configuration
import com.jguerrerope.moviechanllenge.MovieApplication
import com.jguerrerope.moviechanllenge.api.TMDBService
import com.jguerrerope.moviechanllenge.db.MovieDao
import com.jguerrerope.moviechanllenge.db.MovieDatabase
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module(includes = [(ViewModelModule::class)])
class AppModule {
    @Singleton
    @Provides
    fun provideTMDBService(): TMDBService {
        val clientBuilder = OkHttpClient.Builder()
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
        clientBuilder.addInterceptor(loggingInterceptor)

        return Retrofit.Builder()
                .baseUrl(Configuration.TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(clientBuilder.build())
                .build()
                .create<TMDBService>(TMDBService::class.java)
    }

    @Singleton
    @Provides
    fun provideMovieDatabase(app: MovieApplication): MovieDatabase =
            Room.databaseBuilder(app, MovieDatabase::class.java, "movie.db").build()

    @Singleton
    @Provides
    fun provideMovieDatabase(db: MovieDatabase): MovieDao = db.movieDao()
}
