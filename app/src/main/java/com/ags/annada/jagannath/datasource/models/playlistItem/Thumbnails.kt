package com.ags.annada.jagannath.datasource.models.playlistItem

import androidx.room.Embedded

data class Thumbnails(
        @Embedded val medium: Medium?,
)