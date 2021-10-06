package com.diegoparra.movies.di

import android.content.Context
import androidx.room.Room
import com.diegoparra.movies.data.local.MoviesDao
import com.diegoparra.movies.data.local.MoviesDatabase
import com.diegoparra.movies.data.local.MoviesEntityMappers
import com.diegoparra.movies.data.local.MoviesEntityMappersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Singleton
    @Provides
    fun providesMoviesDatabase(@ApplicationContext appContext: Context): MoviesDatabase {
        return Room
            .databaseBuilder(appContext, MoviesDatabase::class.java, MoviesDatabase.DB_NAME)
            .build()
    }

    @Singleton
    @Provides
    fun providesMoviesDao(moviesDatabase: MoviesDatabase): MoviesDao {
        return moviesDatabase.moviesDao()
    }

    @Singleton
    @Provides
    fun providesMoviesEntityMappers(): MoviesEntityMappers {
        return MoviesEntityMappersImpl
    }

}