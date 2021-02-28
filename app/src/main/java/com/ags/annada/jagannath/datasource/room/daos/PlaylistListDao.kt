package com.ags.annada.jagannath.datasource.room.daos

import androidx.room.*
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistListDao {
    @Query("SELECT * from PlaylistListItem")
    fun getAll(): Flow<List<PlaylistListItem>>

    @Query("SELECT * FROM PlaylistListItem WHERE id IN (:ids)")
    fun loadAllByIds(ids: Array<String>): Flow<List<PlaylistListItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg playListListItems: PlaylistListItem)

    @Delete
    fun delete(playListListItem: PlaylistListItem)
}