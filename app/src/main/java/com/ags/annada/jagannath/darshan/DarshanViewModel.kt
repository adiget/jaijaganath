package com.ags.annada.jagannath.darshan

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ags.annada.jagannath.datasource.DarshanRepository
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItemsResponse
import com.ags.annada.jagannath.utils.Event
import com.ags.annada.jagannath.utils.Result
import kotlinx.coroutines.launch
import retrofit2.Response

class DarshanViewModel @ViewModelInject constructor(
    private val repository: DarshanRepository
) : ViewModel() {

    private val _selectItemEvent = MutableLiveData<Event<String>>()
    val selectItemEvent: LiveData<Event<String>> = _selectItemEvent

    var selectedPlayListItem: PlaylistItem? = null

    val playlistItemLiveData: MutableLiveData<Result<PlaylistItemsResponse>> = MutableLiveData()

    private val _snackbarText = MutableLiveData<Event<Int>>()
    val snackbarText: LiveData<Event<Int>> = _snackbarText

    fun onClickItem(item: PlaylistItem) {
        selectedPlayListItem = item
        _selectItemEvent.value = Event(item.snippet.resourceId.videoId)
    }

    fun getPlaylistItems(playlistId: String) = viewModelScope.launch {
        playlistItemLiveData.postValue(Result.Loading)
        val response = repository.getAllPlaylistItems(playlistId)
        playlistItemLiveData.postValue(handlePlaylistItemResponse(response))
    }

    private fun handlePlaylistItemResponse(response: Response<PlaylistItemsResponse>): Result<PlaylistItemsResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Result.Success(it)
            }
        }

        return Result.Error(Exception())
    }
}