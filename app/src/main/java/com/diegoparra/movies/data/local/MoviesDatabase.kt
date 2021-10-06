package com.diegoparra.movies.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.diegoparra.movies.data.local.entities.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class MoviesDatabase: RoomDatabase() {

    companion object {
        const val DB_NAME = "com.diegoparra.movies.moviesdb"
    }

    abstract fun moviesDao(): MoviesDao

}