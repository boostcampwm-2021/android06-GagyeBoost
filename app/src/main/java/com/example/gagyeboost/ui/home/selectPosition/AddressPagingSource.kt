package com.example.gagyeboost.ui.home.selectPosition

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.model.remote.KakaoAPIService
import com.google.android.gms.maps.model.LatLng

private const val STARTING_PAGE_INDEX = 1

class AddressPagingSource(
    private val service: KakaoAPIService,
    private val keyword: String,
    private val latLng: LatLng,
    private val callback: (Boolean) -> Unit
) : PagingSource<Int, PlaceDetail>() {

    override fun getRefreshKey(state: PagingState<Int, PlaceDetail>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaceDetail> {
        return try {
            val next = params.key ?: STARTING_PAGE_INDEX
            val response = service.fetchPlaceListFromKeyword(
                keyword,
                next,
                latLng.latitude.toString(),
                latLng.longitude.toString()
            )
            val body = response.body()
            val nextKey = if (body?.meta?.isEnd == null) null else next + 1

            if (body != null) {
                if (body.meta.isEnd && body.documents.isEmpty()) {
                    callback(true)
                    LoadResult.Error(Exception())
                } else if (body.meta.isEnd) {
                    callback(false)
                    LoadResult.Page(
                        data = body.documents,
                        prevKey = if (next == STARTING_PAGE_INDEX) null else next - 1,
                        nextKey = null
                    )
                } else {
                    callback(false)
                    LoadResult.Page(
                        data = body.documents,
                        prevKey = if (next == STARTING_PAGE_INDEX) null else next - 1,
                        nextKey = nextKey
                    )
                }
            } else {
                callback(true)
                LoadResult.Error(Exception())
            }
        } catch (e: Exception) {
            callback(false)
            LoadResult.Error(e)
        }
    }
}
