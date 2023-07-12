package com.example.myapplication.interfaces

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface APIService {
    @POST("send")
    fun sendMessage(
        @HeaderMap header: HashMap<String, String>,
        @Body bodyMessage: String
    ): Call<String>

}