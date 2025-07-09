package com.example.piratv.apis

import com.example.piratv.BuildConfig
import com.example.piratv.models.SearchResponse
import com.example.piratv.models.SearchResponseTvSeries
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TheMoviesDBAPI {

    @Headers("Authorization: Bearer ${BuildConfig.AUTH_KEY}")
    @GET("search/multi")
    fun searchTitles(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("include_adult") includeAdult: Boolean = false,
        ): Call<SearchResponse>

    @GET("discover/movie")
    fun getMovies(
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("api_key") page: String = BuildConfig.API_KEY_THEMOVIEDB,
        @Query("with_genres") withGenres: Int = 10770,
        @Query("include_adult") includeAdult: Boolean = false
    ): Call<SearchResponse>

    @GET("discover/tv")
    fun getSeries(
        @Query("sort_by") sortBy: String = "popularity.desc",
        @Query("api_key") page: String = BuildConfig.API_KEY_THEMOVIEDB,
        @Query("with_genres") withGenres: Int = 10759,
        @Query("include_adult") includeAdult: Boolean = false
    ): Call<SearchResponse>

    @Headers("Authorization: Bearer ${BuildConfig.AUTH_KEY}")
    @GET("tv/{series_id}")
    fun getSeriesDetails(
        @Path("series_id") seriesId: Int,
        @Query("language") language: String = "en-US"
    ): Call<SearchResponseTvSeries>

}
