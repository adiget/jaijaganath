package com.ags.annada.jagannath.darshan.details

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.ags.annada.jagannath.datasource.models.video.VideosResponse
import com.ags.annada.jagannath.datasource.repository.DarshanRepository
import com.ags.annada.jagannath.utils.Event
import kotlinx.coroutines.launch

class DarshanPlayerViewModel @ViewModelInject constructor(
    private val repository: DarshanRepository,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    val videoId = savedStateHandle.get<String>(KEY_VIDEO_ID)
    val videoTitle = savedStateHandle.get<String>(KEY_VIDEO_TITLE)
    val videoDescription = savedStateHandle.get<String>(KEY_VIDEO_DESCRIPTION)
    var videosResponse: MutableLiveData<VideosResponse> = MutableLiveData()
    val viewCount = videosResponse.map { it.items?.get(0)?.statistics?.viewCount }
    val likeCount = videosResponse.map { it.items?.get(0)?.statistics?.likeCount }
    val dislikeCount = videosResponse.map { it.items?.get(0)?.statistics?.dislikeCount }

    private val _shareVideoEvent = MutableLiveData<Event<Unit>>()
    val shareVideoEvent: LiveData<Event<Unit>> = _shareVideoEvent

    private val _rateVideoEvent = MutableLiveData<Event<Unit>>()
    val rateVideoEvent: LiveData<Event<Unit>> = _rateVideoEvent

    fun getVideoStatistics() = viewModelScope.launch {
        val response = repository.getVideoStatistics(videoId)
        videosResponse.postValue(response?.body())
    }

    fun shareVideo() {
        _shareVideoEvent.value = Event(Unit)
    }

    fun rateVideo() {
        _rateVideoEvent.value = Event(Unit)
    }
}