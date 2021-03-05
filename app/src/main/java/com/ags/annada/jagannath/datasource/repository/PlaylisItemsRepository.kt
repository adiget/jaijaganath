package com.ags.annada.jagannath.datasource.repository

import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItemsResponse
import com.ags.annada.jagannath.datasource.network.api.ApiResponse
import com.ags.annada.jagannath.datasource.network.api.ApiService
import com.ags.annada.jagannath.datasource.network.api.ApiSuccessResponse
import com.ags.annada.jagannath.datasource.room.daos.PlaylistItemDao
import com.ags.annada.jagannath.utils.RateLimiter
import com.ags.annada.jagannath.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PlaylisItemsRepository @Inject constructor(
        private val apiService: ApiService,
        private val playlistItemDao: PlaylistItemDao
) {
    private val playlistItemRateLimit = RateLimiter<String>(5, TimeUnit.MINUTES)

    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getPlaylistItems(playlistId: String, nextPageToken: String): Flow<Resource<List<PlaylistItem>>> {
        return object : NetworkBoundResource<List<PlaylistItem>, PlaylistItemsResponse>() {
            override suspend fun saveCallResult(response: PlaylistItemsResponse) {
                playlistItemDao.insertAll(playListItems = response.playlistItem2s.toTypedArray())
            }

            override fun shouldFetch(data: List<PlaylistItem>?): Boolean {
                return data == null || data.isEmpty() || playlistItemRateLimit.shouldFetch(playlistId)
            }

            override fun loadFromDb() = playlistItemDao.getAll()

            fun getPageAndNext(playlistId: String, nextPageToken: String): Flow<ApiResponse<PlaylistItemsResponse>> {
                return apiService.getAllPlayListItemsForPlayListId(playlistId = playlistId, pageToken = nextPageToken)
                        .flatMapLatest { apiResponse ->
                            var nextPage: String? = null

                            if (apiResponse is ApiSuccessResponse) {
                                nextPage = apiResponse.body.nextPageToken
                            }

                            if (nextPage == null) {
                                flowOf(apiResponse)
                            } else {
                                getPageAndNext(playlistId, nextPage)
                            }
                        }
            }

            override suspend fun createCall() = getPageAndNext(playlistId = playlistId, nextPageToken = nextPageToken)

            override fun onFetchFailed() {
                playlistItemRateLimit.reset(playlistId)
            }
        }.asFlow()
    }
}