package com.example.gagyeboost.model.data

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(
    val results: List<PlaceDetail>,
    val status: String
)

data class PlaceDetail(
    @SerializedName("formatted_address")
    val formattedAddress: String,
    val name: String,
    val geometry: Geometry
)

data class Geometry(
    val location: LatLng
)

data class LatLng(
    val lat: Double,
    val lng: Double
)
