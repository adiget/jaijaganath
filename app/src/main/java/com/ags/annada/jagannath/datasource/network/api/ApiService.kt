package com.ags.annada.jagannath.datasource.network.api

import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListResponse
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItemsResponse
import com.ags.annada.jagannath.datasource.models.video.VideosResponse
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.VIDEOS_PART
import kotlinx.coroutines.flow.Flow
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET(Contracts.PLAYLIST_ENDPOINT)
    fun getPlayLists(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String
    ): Flow<ApiResponse<PlaylistListResponse>>

    @GET(Contracts.PLAYLIST_ITEM_ENDPOINT)
    fun getAllPlayListItemsForPlayListId(
        @Query("part") part: String = "snippet,status",
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String? = "",
        @Query("maxResults") maxResults: Int? = 50
    ): Flow<ApiResponse<PlaylistItemsResponse>>

    @GET(Contracts.PLAYLIST_ITEM_ENDPOINT)
    suspend fun getPlayListItems(
        @Query("part") part: String = "snippet,status",
        @Query("playlistId") playlistId: String,
        @Query("maxResults") maxResults: Int? = 50
    ): Response<PlaylistItemsResponse>

    @GET(Contracts.PLAYLIST_ITEM_ENDPOINT)
    suspend fun getPlayListItemsForPage(
        @Query("part") part: String = "snippet,status",
        @Query("playlistId") playlistId: String,
        @Query("pageToken") pageToken: String,
        @Query("maxResults") maxResults: Int? = 50
    ): Response<PlaylistItemsResponse>

    @GET(Contracts.VIDEOS_ENDPOINT)
    suspend fun getVideoStatisticsByVideoId(
        @Query("part") part: String = VIDEOS_PART,
        @Query("id") videoId: String
    ): Response<VideosResponse>
}