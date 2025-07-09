package com.example.piratv.apis

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientTheMoviesDB {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    val theMoviesDBAPI: TheMoviesDBAPI by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TheMoviesDBAPI::class.java)
    }

}