package com.ags.annada.jagannath.datasource.models.video

data class VideosResponse(
        val etag: String,
        val items: List<Item>?,
        val kind: String,
        val pageInfo: PageInfo
)