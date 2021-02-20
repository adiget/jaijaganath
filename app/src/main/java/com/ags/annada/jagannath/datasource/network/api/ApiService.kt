package com.ags.annada.jagannath.datasource.network.api

import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItemsResponse
import com.ags.annada.jagannath.datasource.models.video.VideosResponse
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.API_KEY
import com.ags.annada.jagannath.datasource.network.api.Contracts.Companion.VIDEOS_PART
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(Contracts.PLAYLIST_ITEM_ENDPOINT)
    suspend fun getPlayListItems(
        @Query("part") part: String = "snippet,status",
        @Query("playlistId") playlistId: String,
        @Query("key") api_key: String = API_KEY,
        @Query("maxResults") maxResults: Int? = 50
    ): Response<PlaylistItemsResponse>

    @GET(Contracts.PLAYLIST_ITEM_ENDPOINT)
    suspend fun getPlayListItemsForPage(
        @Query("part") part: String = "snippet,status",
        @Query("playlistId") playlistId: String,
        @Query("key") api_key: String = API_KEY,
        @Query("pageToken") pageToken: String,
        @Query("maxResults") maxResults: Int? = 50
    ): Response<PlaylistItemsResponse>

    @GET(Contracts.VIDEOS_ENDPOINT)
    suspend fun getVideoStatisticsByVideoId(
        @Query("part") part: String = VIDEOS_PART,
        @Query("id") videoId: String,
        @Query("key") api_key: String = API_KEY
    ): Response<VideosResponse>
}