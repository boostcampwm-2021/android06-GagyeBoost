package com.example.gagyeboost.model.remote

import com.example.gagyeboost.BuildConfig
import com.example.gagyeboost.model.data.PlaceDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAPIService {

    @GET("textsearch/json")
    suspend fun getPlaceListFromKeyword(
        @Query("query") input: String,
        @Query("key") key: String = BuildConfig.google_map_key
    ): Response<PlaceDetailResponse>
}
