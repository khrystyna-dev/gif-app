package com.example.gif_app

import retrofit2.http.GET
import retrofit2.http.Query

interface DataService {
    @GET("gifs/trending?api_key=6uGTMfdu5AHnLHozRgBbldVaDJSwjfe8")
    fun getGifs(): retrofit2.Call<DataResult>

    @GET("gifs/trending?api_key=6uGTMfdu5AHnLHozRgBbldVaDJSwjfe8")
    fun getTrendingGifs(): retrofit2.Call<DataResult>

    @GET("gifs/search?api_key=6uGTMfdu5AHnLHozRgBbldVaDJSwjfe8")
    fun searchGifs(@Query("q") query: String): retrofit2.Call<DataResult>
}