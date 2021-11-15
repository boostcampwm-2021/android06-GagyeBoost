package com.example.gagyeboost.model.data

import com.google.gson.annotations.SerializedName

data class PlaceDetailResponse(
    val meta: PlaceMeta,
    val documents: List<PlaceDetail>
)

data class PlaceDetail(
    val id: String,
    @SerializedName("address_name")
    val addressName: String,
    @SerializedName("category_group_code")
    val categoryGroupCode: String,
    @SerializedName("category_group_name")
    val categoryGroupName: String,
    @SerializedName("category_name")
    val categoryName: String,
    val distance: String,
    val phone: String,
    @SerializedName("place_name")
    val placeName: String,
    @SerializedName("road_address_name")
    val roadAddressName: String,
    @SerializedName("y")
    val lat: String,
    @SerializedName("x")
    val lng: String
)

data class PlaceMeta(
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("pageable_count")
    val pageableCount: Int,
    @SerializedName("is_end")
    val isEnd: Boolean
)
