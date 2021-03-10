package com.ags.annada.jagannath.datasource.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItemsResponse
import com.ags.annada.jagannath.datasource.network.api.ApiResponse
import com.ags.annada.jagannath.datasource.network.api.ApiService
import com.ags.annada.jagannath.datasource.network.api.ApiSuccessResponse
import com.ags.annada.jagannath.datasource.room.daos.PlaylistItemDao
import com.ags.annada.jagannath.utils.RateLimiter
import com.ags.annada.jagannath.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylisItemsRepository @Inject constructor(
    private val apiService: ApiService,
    private val playlistItemDao: PlaylistItemDao,
    private val dataStore: DataStore<Preferences>
) {
    private val playlistItemRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    val nextPageTokenFlow: Flow<String>? = dataStore.data
        .catch { emit(emptyPreferences()) }
        .map { preferences ->
            preferences[NEXT_PAGE_TOKEN_KEY] ?: ""
        }

    @FlowPreview
    @ExperimentalCoroutinesApi
    suspend fun getPlaylistItems(
        playlistId: String,
        nextPageToken: String?
    ): Flow<Resource<List<PlaylistItem>>> {
        return object : NetworkBoundResource<List<PlaylistItem>, PlaylistItemsResponse>() {
            override suspend fun saveCallResult(response: PlaylistItemsResponse) {
                playlistItemDao.insertAll(playListItems = response.playlistItem2s.filter {
                    it.status.privacyStatus.contentEquals(
                        "public"
                    )
                }.sortedByDescending { it.snippet.publishedAt }.toTypedArray())
            }

            override fun shouldFetch(data: List<PlaylistItem>?): Boolean {
                return data == null || data.isEmpty() || playlistItemRateLimit.shouldFetch(
                    playlistId
                )
            }

            override fun loadFromDb() = playlistItemDao.getAll()

            fun getPageAndNext(
                playlistId: String,
                nextPageToken: String?
            ): Flow<ApiResponse<PlaylistItemsResponse>> {
                return apiService.getAllPlayListItemsForPlayListId(
                    playlistId = playlistId,
                    pageToken = nextPageToken
                )
                    .flatMapLatest { apiResponse ->
                        val successResponse: ApiSuccessResponse<PlaylistItemsResponse> =
                            apiResponse.takeIf { it is ApiSuccessResponse } as ApiSuccessResponse<PlaylistItemsResponse>

                        val nextPage: String? = successResponse.body.nextPageToken

                        nextPage?.let { getPageAndNext(playlistId, it) } ?: run {
                            nextPageToken?.let { setNextPageToken(nextPageToken) }
                            flowOf(apiResponse)
                        }
                    }
            }

            override suspend fun createCall(): Flow<ApiResponse<PlaylistItemsResponse>> {
                return getPageAndNext(playlistId = playlistId, nextPageToken = nextPageToken)
            }

            override fun onFetchFailed() {
                playlistItemRateLimit.reset(playlistId)
            }
        }.asFlow()
    }

    suspend fun setNextPageToken(nextPageToken: String) {
        dataStore.edit { preferences ->
            preferences[NEXT_PAGE_TOKEN_KEY] = nextPageToken
        }
    }
}

private val NEXT_PAGE_TOKEN_KEY = stringPreferencesKey("next_pagetoken")