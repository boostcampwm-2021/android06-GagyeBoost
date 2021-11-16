package com.example.gagyeboost.model.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class KakaoAPIClient(
    private val httpLoggingInterceptor: HttpLoggingInterceptor,
    private val headerInterceptor: HeaderInterceptor
) {

    private var service: KakaoAPIService? = null

    fun getKakaoApiService(): KakaoAPIService {
        service?.let {
            return it
        } ?: run {
            val client = OkHttpClient.Builder().connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(headerInterceptor)
                .build()

            service = Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(KakaoAPIService::class.java)

            return service!!
        }
    }

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
    }
}
