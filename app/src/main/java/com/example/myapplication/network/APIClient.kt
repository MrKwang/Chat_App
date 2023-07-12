package com.example.myapplication.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

class APIClient {
    private var retrofit: Retrofit? = null
    fun getClient(): Retrofit? {
        if (retrofit == null){
            retrofit = Retrofit.Builder()
                .baseUrl("https://fcm.googleapis.com/fcm/send")
                .addConverterFactory(ScalarsConverterFactory.create())
                .build()
        }
        return retrofit
    }

}