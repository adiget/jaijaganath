package com.ags.annada.jagannath.datasource.models.playlistItem

import com.google.gson.annotations.SerializedName


data class PlaylistItemsResponse(
    val etag: String,
    @SerializedName("items")
    var playlistItem2s: List<PlaylistItem>,
    val kind: String,
    val nextPageToken: String,
    val pageInfo: PageInfo
)

