package com.example.gagyeboost.ui.home.selectPosition

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.gagyeboost.model.data.PlaceDetail
import com.example.gagyeboost.model.data.ResultType
import com.example.gagyeboost.model.remote.KakaoAPIService
import com.google.android.gms.maps.model.LatLng

class AddressPagingSource(
    private val service: KakaoAPIService,
    private val keyword: String,
    private val latLng: LatLng,
    private val callback: (ResultType) -> Unit
) : PagingSource<Int, PlaceDetail>() {

    override fun getRefreshKey(state: PagingState<Int, PlaceDetail>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PlaceDetail> {
        return try {
            callback(ResultType.OK)
            val next = params.key ?: 1
            val response = service.fetchPlaceListFromKeyword(
                keyword,
                next,
                latLng.latitude.toString(),
                latLng.longitude.toString()
            )

            response.body()?.let {
                callback(ResultType.OK)
                if (it.meta.isEnd && it.documents.isEmpty()) {
                    LoadResult.Error(Exception())
                } else if (it.meta.isEnd) {
                    LoadResult.Page(
                        data = it.documents,
                        prevKey = if (next == 1) null else next - 1,
                        nextKey = null
                    )
                } else {
                    LoadResult.Page(
                        data = it.documents,
                        prevKey = if (next == 1) null else next - 1,
                        nextKey = next + 1
                    )
                }
            } ?: run {
                callback(ResultType.NullData)
                LoadResult.Error(Exception())
            }
        } catch (e: Exception) {
            callback(ResultType.LoadFail)
            LoadResult.Error(e)
        }
    }
}
