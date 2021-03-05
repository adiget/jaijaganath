package com.ags.annada.jagannath.datasource.models.playlistItem

import androidx.room.Embedded

data class Snippet(
        val channelTitle: String,
        val description: String,
        val playlistId: String,
        val position: Int,
        val publishedAt: String,
        @Embedded val resourceId: ResourceId,
        @Embedded val thumbnails: Thumbnails?,
        val title: String
)