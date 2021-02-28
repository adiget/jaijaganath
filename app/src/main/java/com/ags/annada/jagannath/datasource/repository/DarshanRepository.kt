package com.ags.annada.jagannath.datasource.repository

import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItemsResponse
import com.ags.annada.jagannath.datasource.network.api.ApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DarshanRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun getAllPlaylistItems(playlistId: String): Response<PlaylistItemsResponse> {
        val allPlatListItemsResponse: Response<PlaylistItemsResponse> = getPlaylistItems(playlistId)
        val allPlatListItems = mutableListOf<PlaylistItem>()
        allPlatListItemsResponse.body()?.playlistItem2s?.let { allPlatListItems.addAll(it) }

        var nextPageToken = allPlatListItemsResponse.body()?.nextPageToken ?: ""

        while (nextPageToken != "") {
            // Retrieve next set of items in the playlist.
            val response = apiService.getPlayListItemsForPage(
                playlistId = playlistId,
                pageToken = nextPageToken
            )

            if (response.isSuccessful) {
                response.body()?.playlistItem2s?.let { allPlatListItems.addAll(it) }
            }

            nextPageToken = response.body()?.nextPageToken ?: ""
        }

        allPlatListItemsResponse.body()?.playlistItem2s = allPlatListItems

        return allPlatListItemsResponse
    }

    suspend fun getPlaylistItems(playlistId: String) =
        apiService.getPlayListItems(playlistId = playlistId)

    suspend fun getVideoStatistics(videoId: String?) =
        videoId?.let { apiService.getVideoStatisticsByVideoId(videoId = it) }
}