package com.diegoparra.movies.di

import com.diegoparra.movies.data.MoviesDtoToEntityMappers
import com.diegoparra.movies.data.MoviesDtoToEntityMappersImpl
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
            /*
                Note: There is a small problem in the api.
                Query params order is affecting the results, for example, when querying popular movies by:
                    https://api.themoviedb.org/3/movie/popular?api_key=0189ad5be185d0efe0ef8b1ccd3c7462&language=de&page=1
                    https://api.themoviedb.org/3/movie/popular?&page=1&api_key=0189ad5be185d0efe0ef8b1ccd3c7462&language=de
                Even when the results are the same, the first request is sorted by popularity but
                the second not.
                For that reason, I made a small change here when adding the api_key to the queries:
                I add them before any other queryParam coming from original query.

                It is working for the popular movies query, and may be working for other api queries
                as well, as I think the api was developed consistently.
                However, when adding another query to MoviesApi, test the case, and if it turns out
                to get a similar issue, a possible solution may be get ridding of this piece of code
                and add the api_key directly on each query in MoviesApi in the desired query param
                order position.
             */
            val queryParams = originalUrl.queryParameterNames.map {
                Pair(it, originalUrl.queryParameter(it))
            }
            val url = originalUrl.newBuilder()
                .apply { queryParams.forEach { removeAllQueryParameters(it.first) } }
                .addQueryParameter("api_key", MoviesApi.TMDB_API_KEY)
                .addQueryParameter("language", Locale.getDefault().language)
                .apply { queryParams.forEach { addQueryParameter(it.first, it.second) } }
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

    @Singleton
    @Provides
    fun providesMoviesDtoToEntityMappers(): MoviesDtoToEntityMappers {
        return MoviesDtoToEntityMappersImpl
    }

}

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class LoggingInterceptor

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class AuthInterceptor