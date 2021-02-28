package com.ags.annada.jagannath.datasource.repository

import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListResponse
import com.ags.annada.jagannath.datasource.network.api.ApiService
import com.ags.annada.jagannath.datasource.room.daos.PlaylistListDao
import com.ags.annada.jagannath.utils.RateLimiter
import com.ags.annada.jagannath.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistListRepository @Inject constructor(
    private val apiService: ApiService,
    private val playlistListDao: PlaylistListDao
) {
    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getPlaylistList(channelId: String): Flow<Resource<List<PlaylistListItem>>> {
        return object : NetworkBoundResource<List<PlaylistListItem>, PlaylistListResponse>() {
            override suspend fun saveCallResult(response: PlaylistListResponse) {
                playlistListDao.insertAll(playListListItems = response.playListListItems.toTypedArray())
            }

            override fun shouldFetch(data: List<PlaylistListItem>?): Boolean {
                return data == null || data.isEmpty() || repoListRateLimit.shouldFetch(channelId)
            }

            override fun loadFromDb() = playlistListDao.getAll()

            override suspend fun createCall() = apiService.getPlayLists(channelId = channelId)

            override fun onFetchFailed() {
                repoListRateLimit.reset(channelId)
            }
        }.asFlow()
    }
}