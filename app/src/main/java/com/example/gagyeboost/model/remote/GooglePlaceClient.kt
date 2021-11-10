package com.example.gagyeboost.model.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class GooglePlaceClient {

    private var service: GooglePlaceService? = null

    fun getGooglePlayService(): GooglePlaceService {
        service?.let {
            return it
        } ?: run {
            val client = OkHttpClient.Builder().connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build()

            service = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(GooglePlaceService::class.java)

            return service!!
        }
    }

    companion object {
        const val BASE_URL = "https://maps.googleapis.com/maps/api/place/findplacefromtext/json"
    }
}
