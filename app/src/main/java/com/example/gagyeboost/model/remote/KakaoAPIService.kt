package com.example.gagyeboost.model.remote

import com.example.gagyeboost.model.data.PlaceDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface KakaoAPIService {

    @GET("v2/local/search/keyword.json")
    suspend fun fetchPlaceListFromKeyword(
        @Query("query") input: String,
        @Query("page") page: Int,
        @Query("y") lat: String,
        @Query("x") lng: String,
        @Query("radius") radius: Int = 20000
    ): Response<PlaceDetailResponse>
}
