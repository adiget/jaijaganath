package com.ags.annada.jagannath.datasource.models.playlistItem


import java.io.Serializable

data class PlaylistItem(
    val etag: String,
    val id: String,
    val kind: String,
    val snippet: Snippet,
    val status: Status
) : Serializable