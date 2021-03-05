package com.ags.annada.jagannath.datasource.models.playlistItem

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class PlaylistItem(
        @PrimaryKey val id: String,
        @Embedded val snippet: Snippet,
        @Embedded val status: Status
) : Serializable