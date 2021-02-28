package com.ags.annada.jagannath.datasource.room

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannath.datasource.room.daos.PlaylistListDao

@Database(
    entities = [
        PlaylistListItem::class
    ], version = 1
)
@TypeConverters(RoomTypeConverters::class)
abstract class JaiJaganathDatabase : RoomDatabase() {
    abstract fun playlistListDao(): PlaylistListDao
}