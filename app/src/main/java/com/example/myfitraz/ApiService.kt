package com.example.myfitraz
import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("path/to/your/endpoint") // Replace with your API endpoint
    fun getMotivationalQuote(): Call<QuoteResponse> // Replace with your response model
}
