package com.ags.annada.jagannath.datasource.room.daos

import androidx.room.*
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistItemDao {
    @Query("SELECT * from PlaylistItem")
    fun getAll(): Flow<List<PlaylistItem>>

    @Query("SELECT * FROM PlaylistItem WHERE id IN (:ids)")
    fun loadAllByIds(ids: Array<String>): Flow<List<PlaylistItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg playListItems: PlaylistItem)

    @Delete
    fun delete(playListItem: PlaylistItem)
}