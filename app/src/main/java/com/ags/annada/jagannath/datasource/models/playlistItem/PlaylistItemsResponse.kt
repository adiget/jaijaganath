package com.ags.annada.jagannath.datasource.models.playlistItem

import com.google.gson.annotations.SerializedName

data class PlaylistItemsResponse(
        @SerializedName("items")
        var playlistItem2s: List<PlaylistItem>,
        val nextPageToken: String?
        //val pageInfo: PageInfo
)

