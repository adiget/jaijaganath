package com.ags.annada.jagannath.datasource.models.playlist

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class PlaylistListResponse(
    @SerializedName("items")
    val playListListItems: List<PlaylistListItem>,
)

@Entity
data class PlaylistListItem(
    @PrimaryKey val id: String,
    @Embedded val snippet: Snippet
)

data class Snippet(
    val publishedAt: String,
    @Embedded val thumbnails: Thumbnails,
    val title: String
)

data class Thumbnails(
    @Embedded val medium: Medium
)

data class Medium(
    val height: Int,
    val url: String,
    val width: Int
)