package com.diegoparra.movies.di

import com.diegoparra.movies.data.network.MoviesApi
import com.diegoparra.movies.data.network.MoviesDtoMappers
import com.diegoparra.movies.data.network.MoviesDtoMappersImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    @LoggingInterceptor
    fun providesLoggingInterceptor(): Interceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    @AuthInterceptor
    fun providesAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val originalUrl = originalRequest.url
            val url = originalUrl.newBuilder()
                .addQueryParameter("api_key", MoviesApi.TMDB_API_KEY)
                .addQueryParameter("language", Locale.getDefault().language)
                .build()
            val newRequest = originalRequest.newBuilder()
                .url(url)
                .build()
            chain.proceed(newRequest)
        }
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(
        @LoggingInterceptor loggingInterceptor: Interceptor,
        @AuthInterceptor authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun providesMoviesApi(client: OkHttpClient): MoviesApi {
        return Retrofit.Builder()
            .baseUrl(MoviesApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MoviesApi::class.java)
    }

    @Singleton
    @Provides
    fun providesMoviesDtoMappers(): MoviesDtoMappers {
        return MoviesDtoMappersImpl
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class AuthInterceptor