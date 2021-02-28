package com.ags.annada.jagannath.home

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ags.annada.jagannath.datasource.models.playlist.PlaylistListItem
import com.ags.annada.jagannath.datasource.repository.PlaylistListRepository
import com.ags.annada.jagannath.utils.Event
import com.ags.annada.jagannath.utils.Resource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.launch

@FlowPreview
@ExperimentalCoroutinesApi
class PlaylistListViewModel @ViewModelInject constructor(
    @Assisted private val savedStateHandle: SavedStateHandle,
    private val playlistListRepository: PlaylistListRepository,
) : ViewModel() {
    val channelId =
        savedStateHandle.getLiveData<String>(ARG_CHANNEL_ID)

    private val _items = MutableLiveData<Resource<List<PlaylistListItem>>>()
    val items: LiveData<Resource<List<PlaylistListItem>>> = _items

    private val _selectItemEvent = MutableLiveData<Event<String>>()
    val selectItemEvent: LiveData<Event<String>> = _selectItemEvent

    init {
        viewModelScope.launch {
            channelId.asFlow().flatMapMerge { id ->
                playlistListRepository.getPlaylistList(id).distinctUntilChanged()
            }.collect {
                _items.value = it
            }
        }
    }

    fun onClickItem(item: PlaylistListItem) {
        _selectItemEvent.value = Event(item.id)
    }
}