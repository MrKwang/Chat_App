package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class APIClient {
    private var retrofit: Retrofit? = null
    fun getClient(): Retrofit? {
        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl("//fcm.googleapis.com/v1/projects/chat-app-f7584/messages:send)")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }
        return retrofit
    }

}