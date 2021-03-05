package com.ags.annada.jagannath.mangalaarti

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ags.annada.jagannath.datasource.models.playlistItem.PlaylistItem
import com.ags.annada.jagannath.datasource.models.video.VideosResponse
import com.ags.annada.jagannath.datasource.repository.DarshanRepository
import com.ags.annada.jagannath.datasource.repository.PlaylisItemsRepository
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
class MangalaAratiViewModel @ViewModelInject constructor(
        @Assisted private val savedStateHandle: SavedStateHandle,
        private val playlistItemsRepository: PlaylisItemsRepository,
        private val darshanRepository: DarshanRepository//review this
) : ViewModel() {
    val playlistId =
            savedStateHandle.getLiveData<String>(ARG_PLAYLIST_ID)

    private val _items = MutableLiveData<Resource<List<PlaylistItem>>>()
    val items: LiveData<Resource<List<PlaylistItem>>> = _items

    val publicItems: LiveData<List<PlaylistItem>?>? = items.map {
        if (it.data?.isNotEmpty() == true) {
            it.data.filter { it.status.privacyStatus == "public" }
        } else {
            null
        }
    }

    val latestItem: LiveData<PlaylistItem?>? = publicItems?.map { it?.sortedByDescending { it.snippet.publishedAt }?.get(0) }

    val videoId = latestItem?.map { it?.snippet?.resourceId?.videoId }?.distinctUntilChanged()
    val videoTitle = latestItem?.map { it?.snippet?.title }
    val videoDescription = latestItem?.map { it?.snippet?.description }

    var videosResponse: MutableLiveData<VideosResponse?>? = MutableLiveData()
    val viewCount = videosResponse?.map { it?.items?.get(0)?.statistics?.viewCount }
    val likeCount = videosResponse?.map { it?.items?.get(0)?.statistics?.likeCount }
    val dislikeCount = videosResponse?.map { it?.items?.get(0)?.statistics?.dislikeCount }

    private val _shareVideoEvent = MutableLiveData<Event<Unit>>()
    val shareVideoEvent: LiveData<Event<Unit>> = _shareVideoEvent

    private val _rateVideoEvent = MutableLiveData<Event<Unit>>()
    val rateVideoEvent: LiveData<Event<Unit>> = _rateVideoEvent


    init {
        viewModelScope.launch {
            playlistId.asFlow().flatMapMerge { id ->
                playlistItemsRepository.getPlaylistItems(id, "").distinctUntilChanged()
            }.collect {
                _items.value = it
            }
        }
    }

    fun getVideoStatistics(videoId: String?) = viewModelScope.launch {
        val response = darshanRepository.getVideoStatistics(videoId)
        videosResponse?.postValue(response?.body())
    }

    fun shareVideo() {
        _shareVideoEvent.value = Event(Unit)
    }

    fun rateVideo() {
        _rateVideoEvent.value = Event(Unit)
    }
}