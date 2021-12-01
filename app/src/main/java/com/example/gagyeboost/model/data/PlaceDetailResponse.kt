package com.example.gagyeboost.model.data

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PlaceDetailResponse(
    val meta: PlaceMeta,
    val documents: List<PlaceDetail>
)

data class PlaceDetail(
    val id: String,
    val distance: String,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("road_address_name")
    val roadAddressName: String,
    @SerializedName("y")
    val lat: String,
    @SerializedName("x")
    val lng: String
): Serializable

data class PlaceMeta(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)
